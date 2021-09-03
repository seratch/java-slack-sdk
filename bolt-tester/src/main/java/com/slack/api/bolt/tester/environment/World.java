package com.slack.api.bolt.tester.environment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class World {
    @Builder.Default
    private String name = "test-" + System.currentTimeMillis();
    @Builder.Default
    private CopyOnWriteArrayList<Organization> organizations = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<Workspace> workspaces = new CopyOnWriteArrayList<>();
    @Builder.Default
    private CopyOnWriteArrayList<AppMetadata> applications = new CopyOnWriteArrayList<>();
}
