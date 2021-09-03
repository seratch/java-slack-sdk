package test_locally.app_tests;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.tester.BoltTester;
import com.slack.api.bolt.tester.environment.AppInstallation;
import com.slack.api.bolt.tester.slackapi.webapi.WebApiResponder;
import com.slack.api.methods.Methods;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.Message;
import com.slack.api.model.event.AppMentionEvent;
import lombok.Data;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SimpleAppTest {

    @Data
    static class YourWebApp {
        private final App app;

        public YourWebApp() {
            App app = new App(AppConfig.builder()
                    // TODO: AppConfig provider that is compatible with both prod env and testing
                    .signingSecret("will be updated by the tester later")
                    .singleTeamBotToken("will bet updated by the tester later")
                    .build()
            );
            app.event(AppMentionEvent.class, (req, ctx) -> {
                ctx.say("Hi!");
                return ctx.ack();
            });
            this.app = app;
        }
    }

    @Test
    public void eventDelivery() throws Exception {
        YourWebApp webApp = new YourWebApp();
        List<WebApiResponder> webApiResponders = new ArrayList<>();
        webApiResponders.add((req) -> {
            if (req.getApiName().equals(Methods.CHAT_POST_MESSAGE)) {
                ChatPostMessageResponse resp = new ChatPostMessageResponse();
                resp.setOk(true);
                // TODO
                resp.setMessage(new Message());
                return Optional.of(resp);
            }
            return Optional.empty();
        });
        BoltTester tester = new BoltTester(webApp.getApp(), webApiResponders);
        try {
            String appManifest = ""; // TODO: YAML parser
            AppInstallation installation = tester.quickSetup("Simple Test", appManifest);
            tester.start();

            AppMentionEvent appMentionEvent = new AppMentionEvent();
            appMentionEvent.setChannel("C111");
            BoltTester.YourWebServerResponse response = tester.deliverEvent(appMentionEvent);
            assertThat(response.getStatusCode(), is(200));
            // verify if `say()` is called
            assertThat(tester.getApiServer().getApiServerRequests(Methods.CHAT_POST_MESSAGE).size(), is(1));

        } finally {
            tester.stop();
        }

    }
}
