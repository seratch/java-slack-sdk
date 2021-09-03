package com.slack.api.bolt.tester.slackapi.server;

import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;
import com.slack.api.bolt.util.QueryStringParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ServletAdapter {
    private ServletAdapter() {
    }

    public static String doReadRequestBodyAsString(HttpServletRequest req) throws IOException {
        return req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static Map<String, List<String>> toHeaderMap(HttpServletRequest req) {
        Map<String, List<String>> headers = new HashMap<>();
        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            List<String> values = Collections.list(req.getHeaders(name));
            headers.put(name, values);
        }
        return headers;
    }

    private static String getApiMethodName(String requestPath) {
        if (requestPath.startsWith("/api/")) {
            return requestPath.split("/api/")[1].split("\\?")[0];
        }
        return null;
    }

    public static Request fromServletRequest(HttpServletRequest req) throws IOException {
        return Request.builder()
                .path(req.getRequestURI())
                .apiName(getApiMethodName(req.getRequestURI()))
                .queryParameters(QueryStringParser.toMap(req.getQueryString()))
                .headers(toHeaderMap(req))
                .textBody(doReadRequestBodyAsString(req))
                .build();
    }

    public static void writeResponse(Response slackResp, HttpServletResponse resp) throws IOException {
        resp.setStatus(slackResp.getStatusCode());
        if (slackResp != null && slackResp.getHeaders() != null) {
            for (Map.Entry<String, List<String>> header : slackResp.getHeaders().entrySet()) {
                String name = header.getKey();
                for (String value : header.getValue()) {
                    resp.addHeader(name, value);
                }
            }
        }
        resp.setHeader("Content-Type", slackResp.getContentType());
        if (slackResp.getBody() != null) {
            resp.getWriter().write(slackResp.getBody());
        }
    }

}
