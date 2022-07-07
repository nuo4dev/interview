package com.hsbc.service;

import com.hsbc.entity.AuthToken;

import java.util.Map;

public class TokenExpiryAction implements Runnable{
    @Override
    public void run() {
        for (Map.Entry<String, AuthToken> e: HTTPServer.userTokenMap.entrySet()) {
            if (e.getValue().isExpired()) {
                String u = e.getKey();
                String token = e.getValue().getToken();
                String s = String.format("user %s with token %s is expired..", u, token);
                System.out.println(s);
                HTTPServer.userTokenMap.remove(e.getKey());
                HTTPServer.tokenUserMap.remove(e.getValue().getToken());
            }
        }
    }
}
