package com.slack.api.bolt.tester.slackapi.server;

import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.tester.slackapi.Request;
import com.slack.api.bolt.tester.slackapi.Response;
import com.slack.api.bolt.tester.slackapi.webapi.WebApiHandler;
import com.slack.api.bolt.tester.slackapi.webhook.WebhookHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.slack.api.bolt.tester.slackapi.server.ServletAdapter.fromServletRequest;
import static com.slack.api.bolt.tester.slackapi.server.ServletAdapter.writeResponse;

@WebServlet
@Slf4j
public class ApiServlet extends HttpServlet {

    private final AppConfig appConfig;
    private final WebApiHandler webApi;
    private final WebhookHandler webhook;

    private CopyOnWriteArrayList<Request> receivedRequests = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<Request> getReceivedRequests() {
        return receivedRequests;
    }

    public ApiServlet(
            AppConfig appConfig,
            WebApiHandler webApi,
            WebhookHandler webhook
    ) {
        this.appConfig = appConfig;
        this.webApi = webApi;
        this.webhook = webhook;
    }

    @Override
    protected void doPost(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) throws IOException {
        Request request = fromServletRequest(httpRequest);
        getReceivedRequests().add(request);
        if (request.getPath().startsWith("/api/")) {
            Response response = this.webApi.handle(request);
            writeResponse(response, httpResponse);
            return;
        }
        if (request.getPath().startsWith("/webhooks/")) {
            Response response = this.webhook.handle(request);
            writeResponse(response, httpResponse);
            return;
        }

        httpResponse.setStatus(400);
        httpResponse.getWriter().write("Not found");
        httpResponse.setContentType("application/json");
    }

}
