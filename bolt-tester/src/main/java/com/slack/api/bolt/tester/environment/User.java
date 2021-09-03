package com.slack.api.bolt.tester.environment;

import com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Builder.Default
    private String id = RandomValueGenerator.generateSlackIDString("W", 11);
    private String name; // TODO
    // admin etc.
}
