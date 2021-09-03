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
public class Request {
    private String host;
    private Integer port;
    private String path;
    private String apiName;
    private Map<String, List<String>> queryParameters;
    private Map<String, List<String>> headers;
    private String textBody;
    private byte[] binaryBody; // TODO
}
