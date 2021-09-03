package com.slack.api.bolt.tester.slackapi.server;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PortFinder {

    private PortFinder() {
    }

    private static final int MINIMUM = 1024;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ConcurrentMap<String, Integer> ALREADY_USED_PORTS = new ConcurrentHashMap<>();

    public static int findNewPort(String name) {
        return ALREADY_USED_PORTS.computeIfAbsent(name, (key) -> findAvailablePort());
    }

    private static int findAvailablePort() {
        while (true) {
            int randomPort = RANDOM.nextInt(9999);
            if (randomPort < MINIMUM) {
                randomPort += MINIMUM;
            }
            if (isAvailable(randomPort)) {
                return randomPort;
            }
        }
    }

    private static boolean isAvailable(int port) {
        try (Socket ignored = new Socket("127.0.0.1", port)) {
            return false;
        } catch (IOException ignored) {
            return true;
        }
    }
}
