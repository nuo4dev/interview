package com.hsbc.service;

import com.hsbc.entity.AuthToken;
import com.hsbc.entity.Role;
import com.hsbc.entity.User;
import com.hsbc.httpEntity.TokenEntity;
import com.hsbc.httpEntity.TokenRole;
import com.hsbc.httpEntity.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleControllerTest {

    UserRoleController controller = new UserRoleController();

    @Test
    void roleCheckHelperSunny() {
        User u1 = new User("tom", "pass");
        controller.createUser(u1);
        controller.addRoleToUser(new UserRole("tom", "role1"));
        AuthToken token = new AuthToken("abc", LocalDateTime.now());
        HTTPServer.tokenUserMap.put("abc", "tom");
        HTTPServer.userTokenMap.put("tom", token);
        TokenRole tr = new TokenRole("abc", "role1");
        Assertions.assertEquals(u1, controller.roleCheckHelper(tr));
    }

    @Test
    void roleCheckHelperException() {
        User u1 = new User("tom", "pass");
        controller.createUser(u1);
        controller.addRoleToUser(new UserRole("tom", "role1"));
        AuthToken token = new AuthToken("abc", LocalDateTime.now());
        HTTPServer.tokenUserMap.put("abc", "tom");
        HTTPServer.userTokenMap.put("tom", token);
        TokenRole tr = new TokenRole("abcd", "role1");
        Assertions.assertThrows(RuntimeException.class, () -> controller.roleCheckHelper(tr));
    }


    @Test
    void createRoleSameNameThrowException() {
        Role r1 = new Role("r1");
        Role r2 = new Role("r1");
        controller.createRole(r1);
        Assertions.assertThrows(RuntimeException.class, () -> controller.createRole(r2));
    }

    @Test
    void authenticate() {
        User u1 = new User("tom", "hardy");
        controller.createUser(u1);
        Assertions.assertEquals(1, controller.userMap.size());
        User stored = controller.userMap.get("tom");
        Assertions.assertEquals(stored.getPassword(), controller.encryptString("hardy"));
        String token = controller.authenticate(u1);
        Assertions.assertTrue(HTTPServer.userTokenMap.containsKey("tom"));
        Assertions.assertEquals("tom", HTTPServer.tokenUserMap.get(token));
    }

    @Test
    void deleteUserShouldFailIfNotExists() {
       Assertions.assertThrows(RuntimeException.class, () -> controller.deleteUser("tom"));
    }

    @Test
    void addRoleToUser() {
        User u1 = new User("tom", "hardy");
        controller.createUser(u1);
        UserRole ur = new UserRole("tom", "role1");
        controller.addRoleToUser(ur);
        Assertions.assertTrue(controller.userMap.get("tom").getRoles().contains(new Role("role1")));
    }

    @Test
    void invalidateToken() {
        User u1 = new User("tom", "hardy");
        controller.createUser(u1);
        String token = controller.authenticate(u1);
        controller.invalidateToken(new TokenEntity(token));
        Assertions.assertFalse(HTTPServer.tokenUserMap.containsKey(token));
    }

}