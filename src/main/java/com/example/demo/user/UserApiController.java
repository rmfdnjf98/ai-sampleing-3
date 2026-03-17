package com.example.demo.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo._core.utils.Resp;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;
    private final HttpSession session;

    // 아이디 중복 체크 API
    @GetMapping("/api/user/username-same-check")
    public ResponseEntity<?> usernameSameCheck(@RequestParam("username") String username) {
        var isAvailable = userService.usernameSameCheck(username);
        return Resp.ok(isAvailable);
    }
}
