package com.slack.api.bolt.tester.environment;

import com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workspace {
    @Builder.Default
    private String id = RandomValueGenerator.generateSlackIDString("T", 11);
    private String name;

    private Organization org;

    @Builder.Default
    private CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<AppInstallation> installations = new CopyOnWriteArrayList<>();
}