package com.slack.api.bolt.tester.slackapi.server;

import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.webapi.DefaultWebApiHandler;
import com.slack.api.bolt.tester.slackapi.webapi.WebApiHandler;
import com.slack.api.bolt.tester.slackapi.webapi.WebApiResponder;
import com.slack.api.bolt.tester.slackapi.webhook.DefaultWebhookHandler;
import com.slack.api.bolt.tester.slackapi.webhook.WebhookHandler;
import com.slack.api.methods.Methods;
import lombok.Data;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
public class ApiServer {

    private int port;
    private Server server;
    private AppConfig appConfig;
    private WebApiHandler webApi;
    private WebhookHandler webhook;
    private CopyOnWriteArrayList<Request> apiServerRequests;

    public ApiServer(
            AppConfig appConfig,
            List<WebApiResponder> responders
    ) {
        this(appConfig, new DefaultWebApiHandler(responders), new DefaultWebhookHandler());
    }

    public ApiServer(
            AppConfig appConfig,
            WebApiHandler webApi,
            WebhookHandler webhook
    ) {
        this(appConfig, PortFinder.findNewPort(buildTestName("bolt-tester")), webApi, webhook);
    }

    public ApiServer(
            AppConfig appConfig,
            int port,
            WebApiHandler webApi,
            WebhookHandler webhook) {
        this.appConfig = appConfig;
        this.webApi = webApi;
        this.webhook = webhook;
        setup(port);
    }

    public List<Request> getApiServerRequests(String apiName) {
        return getApiServerRequests().stream()
                .filter(r -> r.getApiName().equals(apiName))
                .collect(Collectors.toList());
    }

    private static String buildTestName(String prefix) {
        return prefix + System.currentTimeMillis();
    }

    private void setup(int port) {
        this.port = port;
        this.server = new Server(this.port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        ApiServlet servlet = new ApiServlet(this.appConfig, this.webApi, this.webhook);
        this.apiServerRequests = servlet.getReceivedRequests();
        ServletHolder api = new ServletHolder(servlet);
        handler.addServletWithMapping(api, "/*");
    }

    public String getMethodsEndpointPrefix() {
        return "http://127.0.0.1:" + port + "/api/";
    }

    public void start() throws Exception {
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                server.start();
                return;
            } catch (SocketException e) {
                // Just in case, retry starting the server
                setup(PortFinder.findNewPort(buildTestName("bolt-tester")));
                retryCount++;
            }
        }
    }

    public void stop() throws Exception {
        server.stop();
    }

}
