package com.slack.api.bolt.tester.slackapi.webhook;

import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;
import com.slack.api.model.Message;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultWebhookHandler implements WebhookHandler {

    private final CopyOnWriteArrayList<Webhook> webhooks = new CopyOnWriteArrayList<>();

    @Override
    public CopyOnWriteArrayList<Webhook> getAllWebhooks() {
        return this.webhooks;
    }

    @Override
    public Response handle(Request request) {
        Optional<Webhook> maybeWebhook = findWebhook(request);
        if (maybeWebhook.isPresent()) {
            Webhook w = maybeWebhook.get();
            Message newMessage = new Message();
            newMessage.setText("hi");
            w.getChannel().getMessages().add(newMessage);

            if (w.getRemainingCalls() != null) {
                w.setRemainingCalls(w.getRemainingCalls() - 1);
            }
            return Response.builder().body("ok").build();
        }
        return Response.builder().statusCode(403).body("invalid_token").build();
    }

    private Optional<Webhook> findWebhook(Request request) {
        String url = "http://" + request.getHost() + ":" + request.getPort() + request.getPath();
        for (Webhook w : getAllWebhooks()) {
            if (w.getUrl().equals(url)) {
                return Optional.of(w);
            }
        }
        return Optional.empty();
    }
}
