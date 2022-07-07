package com.hsbc.service;

import com.hsbc.entity.AuthToken;
import com.hsbc.entity.User;
import com.hsbc.util.ConfigProperties;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HTTPServer {

    public static Map<String, AuthToken> userTokenMap = new ConcurrentHashMap<>();

    public static Map<String, String> tokenUserMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ConfigProperties props = ConfigProperties.getInstance();
        HttpServer server = HttpServer.create(new InetSocketAddress(props.getHostname(), props.getPort()), 10);
        server.createContext("/hsbc", new CustomHttpHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        new HTTPServer().setupSchedule();
        server.start();
    }

    private void setupSchedule() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(new TokenExpiryAction(), 0, 10, TimeUnit.SECONDS);
    }
}
