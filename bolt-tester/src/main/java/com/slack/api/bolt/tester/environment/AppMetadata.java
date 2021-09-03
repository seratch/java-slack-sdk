package com.slack.api.bolt.tester.environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppMetadata {
    @Builder.Default
    private String id = generateSlackIDString("A", 11);
    private String name;

    @Builder.Default
    private String clientId = generateNumericString(10) + "."  + generateNumericString(13);
    @Builder.Default
    private String clientSecret = generateAlphanumericString(32);
    @Builder.Default
    private String signingSecret = generateAlphanumericString(32);

    @Builder.Default
    private CopyOnWriteArrayList<String> botScopes = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<String> userScopes = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<SlashCommand> slashCommands = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<Shortcut> shortcuts = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<String> botEvents = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<String> userEvents = new CopyOnWriteArrayList<>();
}
