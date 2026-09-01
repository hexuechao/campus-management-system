package com.example.campusmanagement;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String hash = encoder.encode("654321");

        System.out.println(hash);
    }
}
