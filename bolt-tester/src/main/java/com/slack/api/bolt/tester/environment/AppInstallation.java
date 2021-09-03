package com.slack.api.bolt.tester.environment;

import com.slack.api.bolt.tester.slackapi.webhook.Webhook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppInstallation {
    private AppMetadata app;
    private Workspace workspace;
    private Organization organization; // org-wide app

    private String botId;
    private String botToken;

    private String installerUserId;

    private ConcurrentMap<String, String> userTokens;
    private List<Webhook> webhooks;
}
