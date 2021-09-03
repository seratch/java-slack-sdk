package com.slack.api.bolt.tester.slackapi.webapi;

import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.util.json.GsonFactory;

import java.util.Optional;

public interface WebApiResponder {
    Optional<Object> respond(Request request);
}
