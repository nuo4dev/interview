package com.hsbc.entity;

import com.hsbc.util.ConfigProperties;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AuthToken {

    public String getToken() {
        return token;
    }

    public AuthToken() {
    }

    public AuthToken(String token) {
        this.token = token;
    }

    public AuthToken(String token, LocalDateTime initialTime) {
        this.token = token;
        this.initialTime = initialTime;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(LocalDateTime initialTime) {
        this.initialTime = initialTime;
    }

    private String token;

    private LocalDateTime initialTime;

    public boolean isExpired() {
        int tokenExpireTime = ConfigProperties.getInstance().getTokenExpireSeconds();
        return LocalDateTime.now().minus(tokenExpireTime, ChronoUnit.SECONDS).isAfter(initialTime);
    }
}
