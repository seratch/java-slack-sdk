package com.slack.api.bolt.tester.slackapi.webhook;

import com.slack.api.bolt.tester.environment.Channel;
import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;

import java.util.concurrent.CopyOnWriteArrayList;

public interface WebhookHandler {
    CopyOnWriteArrayList<Webhook> getAllWebhooks();

    default Webhook createNewWebhook(Channel channel) {
        Webhook webhook = new Webhook();
        webhook.setChannel(channel);
        this.getAllWebhooks().add(webhook);
        return webhook;
    }

    default Webhook createResponseUrl(Channel channel, String ts) {
        Webhook webhook = new Webhook();
        webhook.setChannel(channel);
        webhook.setTs(ts);
        webhook.setExpireAt(System.currentTimeMillis() + (30 * 60 * 1000));
        webhook.setRemainingCalls(5);
        this.getAllWebhooks().add(webhook);
        return webhook;
    }

    Response handle(Request request);
}
