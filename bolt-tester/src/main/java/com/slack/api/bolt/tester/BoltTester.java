package com.slack.api.bolt.tester;

import com.google.gson.Gson;
import com.slack.api.Slack;
import com.slack.api.SlackConfig;
import com.slack.api.app_backend.SlackSignature;
import com.slack.api.app_backend.events.payload.Authorization;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.tester.environment.*;
import com.slack.api.bolt.tester.slackapi.server.ApiServer;
import com.slack.api.bolt.tester.slackapi.webapi.WebApiResponder;
import com.slack.api.bolt.tester.yourapp.YourAppWebServer;
import com.slack.api.methods.Methods;
import com.slack.api.methods.response.auth.AuthTestResponse;
import com.slack.api.model.event.Event;
import com.slack.api.util.http.UserAgentInterceptor;
import com.slack.api.util.json.GsonFactory;
import lombok.Data;
import okhttp3.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator.generateSlackIDString;

@Data
public class BoltTester {
    private World world;
    private ApiServer apiServer;

    private App boltApp;
    private YourAppWebServer yourServer;

    private AppMetadata currentApp;
    private AppInstallation currentInstallation;

    public BoltTester(App app, List<WebApiResponder> responders) {
        this.boltApp = app;
        this.world = new World();
        this.apiServer = new ApiServer(app.config(), responders);
        this.yourServer = new YourAppWebServer(app);
    }

    public AppInstallation quickSetup(String testName, String appManifestYaml) throws Exception {
        Workspace workspace = createWorkspace(testName + " Workspace");
        AppMetadata app = createApp(testName + " App", appManifestYaml);
        AppInstallation installation = installApp(app, workspace);
        boltApp.config().setSigningSecret(app.getSigningSecret());
        boltApp.config().setSingleTeamBotToken(installation.getBotToken());
        apiServer.getWebApi().getResponders().add((request) -> {
            if (request.getApiName().equals(Methods.AUTH_TEST)) {
                AuthTestResponse response = new AuthTestResponse();
                response.setOk(true);
                response.setAppId(app.getId());
                response.setBotId(installation.getBotId());
                response.setTeamId(workspace.getId());
                return Optional.of(response);
            }
            return Optional.empty();
        });
        return installation;
    }

    public void start() throws Exception {
        this.apiServer.start();
        SlackConfig config = new SlackConfig();
        config.setMethodsEndpointUrlPrefix(this.apiServer.getMethodsEndpointPrefix());
        this.yourServer.getApp().config().setSlack(Slack.getInstance(config));
        this.yourServer.start();
    }

    public void stop() throws Exception {
        this.apiServer.stop();
        this.yourServer.stop();
    }

    public Workspace createWorkspace(String name) {
        Workspace workspace = new Workspace();
        workspace.setName(name);
        this.world.getWorkspaces().add(workspace);
        return workspace;
    }

    public AppMetadata createApp(String name, String appManifestYaml) {
        AppMetadata app = new AppMetadata();
        app.setName(name);
        // TODO
        app.getBotScopes().addAll(Arrays.asList("commands", "chat:write"));
        this.currentApp = app;
        this.world.getApplications().add(app);
        return app;
    }

    public AppInstallation installApp(AppMetadata app, Workspace workspace) {
        AppInstallation installation = new AppInstallation();
        installation.setApp(app);
        installation.setWorkspace(workspace);
        if (app.getBotScopes().size() > 0) {
            installation.setBotId(generateSlackIDString("B", 11));
            installation.setBotToken(generateSlackIDString("xoxb-", 32));
        }
        // TODO: user token
        this.currentInstallation = installation;
        return installation;
    }

    public AppInstallation installOrgWideApp(AppMetadata app, Organization organization) {
        AppInstallation installation = new AppInstallation();
        installation.setApp(app);
        installation.setOrganization(organization);
        this.currentInstallation = installation;
        return installation;
    }

    // TODO: close
    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            // TODO: user agent
            .addInterceptor(new UserAgentInterceptor(Collections.emptyMap()))
            .readTimeout(3_000L, TimeUnit.MILLISECONDS)
            .build();

    private static final Gson GSON = GsonFactory.createSnakeCase();

    @Data
    public static class YourWebServerResponse {
        private Integer statusCode;
        private Map<String, List<String>> headers;
        private String body;
    }

    @Data
    public class EventPayload<E extends Event> {
        private String token;
        private String enterpriseId;
        private String teamId;
        private String apiAppId;
        private String type;
        private List<String> authedUsers;
        private List<String> authedTeams;
        private List<Authorization> authorizations;
        private boolean isExtSharedChannel;
        private String eventId;
        private Integer eventTime;
        private String eventContext;
        private E event;
    }

    public YourWebServerResponse deliverEvent(Event event) throws IOException {
        EventPayload<Event> payload = new EventPayload();
        payload.setType(EventsApiPayload.TYPE);
        payload.setApiAppId(this.currentApp.getId());
        payload.setEventId(generateSlackIDString("Ev", 11));
        payload.setEventTime(Math.round(System.currentTimeMillis() / 1000));
        payload.setTeamId(this.currentInstallation.getWorkspace().getId());
        payload.setToken("legacy-verification-token");
        payload.setEvent(event);
        // TODO; update
        Authorization authorization = new Authorization();
        authorization.setTeamId(this.currentInstallation.getWorkspace().getId());
        authorization.setUserId(this.currentInstallation.getInstallerUserId());
        payload.setAuthorizations(Arrays.asList(authorization));
        String timestamp = String.valueOf(Math.round(System.currentTimeMillis() / 1000));
        String body = GSON.toJson(payload);
        SlackSignature.Generator signatureGenerator = new SlackSignature.Generator(
                boltApp.config().getSigningSecret());
        String signature = signatureGenerator.generate(timestamp, body);
        Request request = new Request.Builder()
                .url(yourServer.getEndpoint())
                .header(SlackSignature.HeaderNames.X_SLACK_REQUEST_TIMESTAMP, timestamp)
                .header(SlackSignature.HeaderNames.X_SLACK_SIGNATURE, signature)
                .post(RequestBody.create(body, MediaType.parse("application/json; charset=utf-8")))
                .build();
        Response response = httpClient.newCall(request).execute();
        YourWebServerResponse httpResponse = new YourWebServerResponse();
        httpResponse.setStatusCode(response.code());
        httpResponse.setHeaders(response.headers().toMultimap());
        httpResponse.setBody(response.body().string());
        return httpResponse;
    }

}
