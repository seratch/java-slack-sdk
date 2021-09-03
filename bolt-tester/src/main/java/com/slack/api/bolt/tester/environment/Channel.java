package com.slack.api.bolt.tester.environment;

import com.slack.api.bolt.tester.slackapi.util.RandomValueGenerator;
import com.slack.api.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @Builder.Default
    private String id = RandomValueGenerator.generateSlackIDString("C", 11);
    private String name;
    private boolean isPrivate;
    // shared channel
    @Builder.Default
    private CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Message> pinnedItems;
}
