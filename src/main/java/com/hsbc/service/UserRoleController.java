package com.hsbc.service;

import com.hsbc.entity.AuthToken;
import com.hsbc.entity.Role;
import com.hsbc.entity.User;
import com.hsbc.httpEntity.TokenEntity;
import com.hsbc.httpEntity.TokenRole;
import com.hsbc.httpEntity.UserRole;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class UserRoleController {

    Map<String,User> userMap = new HashMap<>();

    List<Role> globalRoles = new ArrayList<>();

    public User roleCheckHelper(TokenRole tr) {
        String uname = HTTPServer.tokenUserMap.get(tr.getAuthToken());
        if (uname == null) {
            throw new RuntimeException("Token is invalid");
        }
        return userMap.get(uname);
    }
    public boolean checkRole(TokenRole tr) {
        User u = roleCheckHelper(tr);
        Role rr = new Role(tr.getRoleName());
        return u.getRoles().contains(rr);
    }

    public Set<Role> getAllRoles(TokenRole tr) {
        User u = roleCheckHelper(tr);
        return u.getRoles();
    }

    public User createUser(User user) {
        if (userMap.containsKey(user.getUsername())) {
            throw new RuntimeException("User exists already");
        }
        String encryptPwd = encryptString(user.getPassword());
        User u = new User(user.getUsername(), encryptPwd);
        userMap.put(u.getUsername(), u);
        return u;
    }

    public Role createRole(Role role) {
        if (globalRoles.contains(role)) {
            throw new RuntimeException("Role exists already");
        }
        Role r = new Role(role.getName());
        globalRoles.add(r);
        return r;
    }

    public String authenticate(User user) {
        String encryptPwd = encryptString(user.getPassword());
        User storedUser = userMap.get(user.getUsername());
        if (storedUser == null) {
            throw new RuntimeException("Authentication failed due to user does not exists");
        } else if (!encryptPwd.equals(storedUser.getPassword())) {
            throw new RuntimeException("Authentication failed due to password is invalid");
        }
        AuthToken t = HTTPServer.userTokenMap.get(user.getUsername());
        if (t != null) {
            // refresh cache expire time
            t.setInitialTime(LocalDateTime.now());
        } else {
            t = new AuthToken(randomString(8), LocalDateTime.now());
            HTTPServer.userTokenMap.put(user.getUsername(), t);
            HTTPServer.tokenUserMap.put(t.getToken(), user.getUsername());
        }

        return t.getToken();
    }

    public void deleteUser(String username) {
        User u1 = userMap.get(username);
        if (u1 == null) {
            throw new RuntimeException("Deletion failed due to user does not exists");
        }
        userMap.remove(username);
    }

    public void deleteRole(String roleName) {
        Role r = new Role(roleName);
        boolean isDeleted = globalRoles.remove(r);
        if (!isDeleted) {
            throw new RuntimeException("Deletion failed due to user does not exists");
        }
        // iterate all users to delete this role
        for (Map.Entry<String, User> entry: userMap.entrySet()) {
            Set<Role> userRoles = entry.getValue().getRoles();
            userRoles.remove(r);
        }
    }

    public User addRoleToUser(UserRole ur) {
        Role r = new Role(ur.getRoleName());
        if (!globalRoles.contains(r)) {
            globalRoles.add(r);
        }
        String uname = ur.getUsername();
        User u1 = userMap.get(uname);
        if (u1 == null) {
            throw new RuntimeException("Add role to user failed due to user does not exists");
        }
        u1.getRoles().add(r);
        return u1;
    }

    public void invalidateToken(TokenEntity token) {
        String tokenStr = token.getToken();
        String uname = HTTPServer.tokenUserMap.get(tokenStr);
        if (uname != null) {
            HTTPServer.tokenUserMap.remove(tokenStr);
            HTTPServer.userTokenMap.remove(uname);
        }
    }

    String encryptString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    public static final String AVAILABLE_CHAR="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String randomString(int length){
        StringBuilder sb = new StringBuilder(length);

        int baseLength = AVAILABLE_CHAR.length();
        for (int i = 0; i < length; i++) {
            int number = new Random().nextInt(baseLength);
            sb.append(AVAILABLE_CHAR.charAt(number));
        }
        return sb.toString();
    }

}
