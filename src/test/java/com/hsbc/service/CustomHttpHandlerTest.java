package com.hsbc.service;

import com.hsbc.entity.Role;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomHttpHandlerTest {

    @Test
    public void testJsonParser() {
        Role r = new Role("a");
        List<Role> rs = new ArrayList<>();
        rs.add(r);
        Role rr = new Role("a");
        System.out.println(rs.contains(rr));
    }
}