package com.slack.api.bolt.tester.slackapi.webapi;

import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;
import com.slack.api.util.json.GsonFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultWebApiHandler implements WebApiHandler {

    private final CopyOnWriteArrayList<WebApiResponder> responders;

    public DefaultWebApiHandler(List<WebApiResponder> responders) {
        this.responders = new CopyOnWriteArrayList(responders);
    }

    @Override
    public CopyOnWriteArrayList<WebApiResponder> getResponders() {
        return responders;
    }

    @Override
    public Response handle(Request request) {
        String responseBody = "{\"ok\": false, \"error\":\"invalid_test_settings\"}";
        for (WebApiResponder responder : getResponders()) {
            Optional<Object> maybeResponse = responder.respond(request);
            if (maybeResponse != null && maybeResponse.isPresent()) {
                Object response = maybeResponse.get();
                if (response instanceof String) {
                    responseBody = (String) response;
                } else {
                    responseBody = GsonFactory.createSnakeCase().toJson(response);
                }
                break;
            }
        }
        return Response.builder().body(responseBody).build();
    }
}
