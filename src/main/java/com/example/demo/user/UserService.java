package com.example.demo.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    // 아이디 중복 체크 (true: 사용 가능, false: 중복)
    public boolean usernameSameCheck(String username) {
        var userOp = userRepository.findByUsername(username);
        return userOp.isEmpty();
    }
}
