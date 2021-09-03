package com.slack.api.bolt.tester.yourapp;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;
import com.slack.api.bolt.tester.slackapi.server.PortFinder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.SocketException;

@Slf4j
@Data
public class YourAppWebServer {
    private final App app;
    private int port;
    private Server server;

    public YourAppWebServer(App app) {
        this.app = app;
        setup(PortFinder.findNewPort(buildTestName()));
    }

    public String getEndpoint() {
        return "http://127.0.0.1:" + port + "/slack/events";
    }

    private static String buildTestName() {
        return "test-" + System.currentTimeMillis();
    }

    private void setup(int port) {
        this.port = port;
        this.server = new Server(this.port);
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);
        // TODO: OAuth
        ServletHolder api = new ServletHolder(new SlackAppServlet(app));
        handler.addServletWithMapping(api, "/*");
    }

    public void start() throws Exception {
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                server.start();
                log.info("Test app is running at http://127.0.0.1:{}/slack/events", port);
                return;
            } catch (SocketException e) {
                // Just in case, retry starting the server
                setup(PortFinder.findNewPort(buildTestName()));
                retryCount++;
            }
        }
    }

    public void stop() throws Exception {
        server.stop();
    }
}
