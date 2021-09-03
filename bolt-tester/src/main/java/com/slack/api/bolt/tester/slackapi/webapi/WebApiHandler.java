package com.slack.api.bolt.tester.slackapi.webapi;

import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface WebApiHandler {
    CopyOnWriteArrayList<WebApiResponder> getResponders();
    Response handle(Request request);
}
