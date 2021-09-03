package com.slack.api.bolt.tester.environment;

import com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Organization {
    @Builder.Default
    private String id = RandomValueGenerator.generateSlackIDString("E", 11);
    private String name;
    private List<User> users;
    private List<AppInstallation> installations;
}
