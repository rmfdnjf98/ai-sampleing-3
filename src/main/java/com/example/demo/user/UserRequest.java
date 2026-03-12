package com.example.demo.user;

import lombok.Data;

public class UserRequest {

    @Data
    public static class Login {
        private String username;
        private String password;
    }

}
