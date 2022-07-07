package com.hsbc.httpEntity;

public class TokenRole {

    private String authToken;
    private String roleName;

    public TokenRole() {
    }

    public TokenRole(String authToken, String roleName) {
        this.authToken = authToken;
        this.roleName = roleName;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
