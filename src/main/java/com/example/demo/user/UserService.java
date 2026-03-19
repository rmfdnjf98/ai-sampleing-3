package com.example.demo.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo._core.handler.ex.Exception400;
import com.example.demo.board.BoardRepository;
import com.example.demo.reply.ReplyRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 (비밀번호 암호화 후 저장)
    @Transactional
    public void join(UserRequest.Join reqDTO) {
        // 아이디 중복 체크
        var userOp = userRepository.findByUsername(reqDTO.getUsername());
        if (userOp.isPresent()) {
            throw new Exception400("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화 (BCrypt)
        var encPassword = passwordEncoder.encode(reqDTO.getPassword());
        reqDTO.setPassword(encPassword);

        // 엔티티 변환 후 저장
        userRepository.save(reqDTO.toEntity());
    }

    // 로그인 (비밀번호 검증 후 사용자 객체 반환)
    public User login(UserRequest.Login reqDTO) {
        var userOp = userRepository.findByUsername(reqDTO.getUsername());
        
        // 1. 아이디 존재 확인
        if (userOp.isEmpty()) {
            throw new Exception400("존재하지 않는 회원입니다.");
        }

        User user = userOp.get();

        // 2. 비밀번호 일치 확인 (BCrypt)
        if (!passwordEncoder.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new Exception400("비밀번호가 틀렸습니다.");
        }

        return user;
    }

    // 회원 정보 수정
    @Transactional
    public User update(int id, UserRequest.Update reqDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception400("회원 정보를 찾을 수 없습니다."));

        // 비밀번호 암호화
        String encPassword = passwordEncoder.encode(reqDTO.getPassword());

        // 더티 체킹에 의한 업데이트
        user.setPassword(encPassword);
        user.setEmail(reqDTO.getEmail());
        user.setPostcode(reqDTO.getPostcode());
        user.setAddress(reqDTO.getAddress());
        user.setDetailAddress(reqDTO.getDetailAddress());
        user.setExtraAddress(reqDTO.getExtraAddress());

        return user;
    }

    // 회원 탈퇴
    @Transactional
    public void withdraw(int id) {
        // 1. 내가 쓴 댓글 삭제
        replyRepository.deleteByUserId(id);
        
        // 2. 내 게시글에 달린 모든 댓글(타인이 쓴 것 포함) 삭제
        replyRepository.deleteByBoardUserId(id);

        // 3. 내가 쓴 게시글 삭제
        boardRepository.deleteByUserId(id);

        // 4. 유저 삭제
        userRepository.deleteById(id);
    }

    // 아이디 중복 체크 (true: 사용 가능, false: 중복)
    public boolean usernameSameCheck(String username) {
        var userOp = userRepository.findByUsername(username);
        return userOp.isEmpty();
    }
}
