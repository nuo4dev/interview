package com.hsbc.service;

import com.hsbc.entity.AuthToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

class TokenExpiryActionTest {

    @Test
    public void testTokenExpiry() {
        AuthToken token1 = new AuthToken("abc", LocalDateTime.now().minus(10, ChronoUnit.SECONDS));
        AuthToken token2 = new AuthToken("abcd", LocalDateTime.now().minus(30, ChronoUnit.SECONDS));
        HTTPServer.userTokenMap.put("tom", token1);
        HTTPServer.userTokenMap.put("jack", token2);
        HTTPServer.tokenUserMap.put("abc", "tom");
        HTTPServer.tokenUserMap.put("abcd", "jack");
        TokenExpiryAction action = new TokenExpiryAction();
        action.run();
        Assertions.assertFalse(HTTPServer.userTokenMap.containsKey("jack"));
        Assertions.assertTrue(HTTPServer.userTokenMap.containsKey("tom"));
        Assertions.assertFalse(HTTPServer.tokenUserMap.containsKey("abcd"));
        Assertions.assertTrue(HTTPServer.tokenUserMap.containsKey("abc"));
    }
}