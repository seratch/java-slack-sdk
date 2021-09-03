package com.slack.api.bolt.tester.slackapi.webhook;

import com.slack.api.bolt.tester.environment.Channel;
import lombok.Data;

@Data
public class Webhook {
    private Channel channel;
    private String url; // http://127.0.0.1:{port}/webhooks/...

    private Integer remainingCalls; // non-null only for response_url
    private Long expireAt; // non-null only for response_url
    private String ts; // non-null only for response_url
}
