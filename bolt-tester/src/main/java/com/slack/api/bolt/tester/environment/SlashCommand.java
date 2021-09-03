package com.slack.api.bolt.tester.environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlashCommand {
    private String command;
    private String url;
}
