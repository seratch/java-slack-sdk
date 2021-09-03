package com.slack.api.bolt.tester.slackapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    public static final String DEFAULT_CONTENT_TYPE = "application/json; charset=utf-8";

    @Builder.Default
    private Integer statusCode = 200;
    @Builder.Default
    private String contentType = DEFAULT_CONTENT_TYPE;
    private Map<String, List<String>> headers;
    private String body;
}
