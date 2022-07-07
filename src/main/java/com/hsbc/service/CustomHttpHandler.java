package com.hsbc.service;

import com.google.gson.Gson;
import com.hsbc.entity.AuthToken;
import com.hsbc.entity.Role;
import com.hsbc.entity.User;
import com.hsbc.httpEntity.TokenEntity;
import com.hsbc.httpEntity.TokenRole;
import com.hsbc.httpEntity.UserRole;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class CustomHttpHandler implements HttpHandler {

    private UserRoleController controller;

    private Gson gson;

    public CustomHttpHandler() {
        controller = new UserRoleController();
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {
            handlePOSTRequest(exchange);
        } else if ("DELETE".equals(exchange.getRequestMethod())) {
            handleDeleteRequest(exchange);
        } else if ("PUT".equals(exchange.getRequestMethod())) {
            handlePUTRequest(exchange);
        }
    }

    private void handlePUTRequest(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        String retVal = "";
        TokenRole tr;
        try {
            switch (exchange.getRequestURI().getPath()) {
                case "/hsbc/CheckRole" -> {
                    tr = gson.fromJson(requestBody, TokenRole.class);
                    retVal = String.valueOf(controller.checkRole(tr));
                }
                case "/hsbc/AllRoles" -> {
                    tr = gson.fromJson(requestBody, TokenRole.class);
                    retVal = gson.toJson(controller.getAllRoles(tr));
                }
            }
        } catch (RuntimeException ex) {
            String resp = ex.getMessage();
            writeResponse(400, resp, exchange);
        }
        writeResponse(200, retVal, exchange);
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || query.isEmpty()) {
            writeResponse(400, "you should give predicates for deletion", exchange);
            return;
        }
        String[] qs = query.split("=");
        if (qs.length != 2) {
            writeResponse(400, "you should give predicates for deletion", exchange);
        }
        try {
            switch (exchange.getRequestURI().getPath()) {
                case "/hsbc/User" -> controller.deleteUser(qs[1]);
                case "/hsbc/Role" -> controller.deleteRole(qs[1]);
                default -> {
                }
            }
        } catch (RuntimeException ex) {
            String resp = ex.getMessage();
            writeResponse(400, resp, exchange);
        }
    }

    private void handlePOSTRequest(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        String retVal = "";
        User iu = null;
        try {
            switch (exchange.getRequestURI().getPath()) {
                case "/hsbc/User":
                    iu = gson.fromJson(requestBody, User.class);
                    retVal = gson.toJson(controller.createUser(iu));
                    break;
                case "/hsbc/Role":
                    Role ir = gson.fromJson(requestBody, Role.class);
                    retVal = gson.toJson(controller.createRole(ir));
                    break;
                case "/hsbc/Authenticate":
                    iu = gson.fromJson(requestBody, User.class);
                    retVal = controller.authenticate(iu);
                    break;
                case "/hsbc/AddUserRole":
                    UserRole ur = gson.fromJson(requestBody, UserRole.class);
                    retVal = gson.toJson(controller.addRoleToUser(ur));
                    break;
                case "/hsbc/Invalidate":
                    TokenEntity token = gson.fromJson(requestBody, TokenEntity.class);
                    controller.invalidateToken(token);
                    break;
                default:
                    return;
            }
        } catch (RuntimeException ex) {
            String resp = ex.getMessage();
            writeResponse(400, resp, exchange);
        }
        writeResponse(200, retVal, exchange);
    }

    private void writeResponse(int responseCode, String response, HttpExchange exchange) throws IOException {
        OutputStream os = exchange.getResponseBody();
        exchange.sendResponseHeaders(responseCode, response.length());
        os.write(response.getBytes());
        os.flush();
        os.close();
    }
}
