# 🚩 작업 보고서: T-2.3 회원 정보 수정(Update) 및 탈퇴(Withdraw) 구현

- **작업 일시**: 2026-03-19
- **진행 단계**: 완료

## 1. 🌊 전체 작업 흐름 (Workflow)

```text
[정보 수정 흐름]
사용자 (수정 페이지) -> DTO (Update) -> 서비스 (더티 체킹/암호화) -> DB 업데이트 -> 세션 갱신 -> 홈 이동

[회원 탈퇴 흐름]
사용자 (탈퇴 클릭) -> 탈퇴 확인창 (JS) -> 서비스 (Cascade Delete) -> DB 삭제 -> 세션 무효화 -> 홈 이동
```

1. **DTO 정의**: `UserRequest.Update` 클래스를 생성하여 비밀번호, 이메일, 주소 정보를 관리하도록 설계했습니다.
2. **비즈니스 로직**: `UserService`에 `update`(비밀번호 암호화 및 영속 상태 반영)와 `withdraw`(연관 게시글/댓글 일괄 삭제 후 유저 삭제) 기능을 추가했습니다.
3. **Repository 확장**: `BoardRepository`와 `ReplyRepository`에 유저 ID 기반 삭제 메서드를 추가하여 안전한 데이터 정리를 지원했습니다.
4. **컨트롤러 연동**: 세션 체크 및 데이터 처리를 담당하는 엔드포인트를 추가하고, 정보 수정 시 세션 데이터를 즉시 최신화했습니다.
5. **화면 구현**: 다음 주소 API를 연동한 `update-form.mustache`를 제작하여 사용자 편의성을 높였습니다.

## 2. 🧩 변경된 모든 코드 포함

### 1) DTO (UserRequest.java)
```java
@Data
public static class Update {
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password; // 암호화 처리 예정

    @NotBlank(message = "이메일은 필수입니다")
    private String email;

    private String postcode;
    private String address;
}
```

### 2) Service (UserService.java)
```java
@Transactional
public User update(int id, UserRequest.Update reqDTO) {
    User user = userRepository.findById(id).orElseThrow(...);
    // 비밀번호는 반드시 다시 암호화
    user.setPassword(passwordEncoder.encode(reqDTO.getPassword()));
    user.setEmail(reqDTO.getEmail());
    // 주소 정보 등 업데이트 (더티 체킹)
    return user;
}

@Transactional
public void withdraw(int id) {
    replyRepository.deleteByUserId(id); // 자식 데이터(댓글) 먼저 삭제
    boardRepository.deleteByUserId(id); // 자식 데이터(게시글) 삭제
    userRepository.deleteById(id);      // 부모 데이터(유저) 삭제
}
```

### 3) Controller (UserController.java)
```java
@PostMapping("/user/update")
public String update(UserRequest.Update reqDTO) {
    User updatedUser = userService.update(sessionUser.getId(), reqDTO);
    session.setAttribute("sessionUser", updatedUser); // 헤더 등에 즉시 반영
    return "redirect:/";
}

@PostMapping("/user/withdraw")
public String withdraw() {
    userService.withdraw(sessionUser.getId());
    session.invalidate(); // 깔끔하게 로그아웃 처리
    return "redirect:/";
}
```

## 3. 🍦 상세비유 쉬운 예시 (Easy Analogy)

"이번 작업은 **이사 가기 전 집 정리**와 같습니다.

**정보 수정**은 이사 가기 전 내 연락처나 주소를 관공서에 신고하여 최신 정보로 바꾸는 것과 같아요. 정보를 바꾸면 즉시 내 새로운 명함(세션)이 발급됩니다. **회원 탈퇴**는 아예 동네를 떠나는 것인데, 이때 내가 남겼던 쓰레기나 짐들(댓글, 게시글)을 하나도 남김없이 싹 치우고 나가는 깔끔한 이사와 같습니다!"

## 4. 📚 기술 딥다이브 (Technical Deep-dive)

- **Dirty Checking (더티 체킹)**:
  - **설명**: JPA 영속성 컨텍스트가 관리하는 엔티티의 상태가 변경되면, 트랜잭션 종료 시점에 변경 사항을 감지하여 자동으로 UPDATE 쿼리를 날려주는 기술입니다. 명시적인 `save()` 호출 없이 객체의 값만 바꿔도 DB에 반영됩니다.
  
- **Cascade Delete (연쇄 삭제)**:
  - **설명**: 부모 데이터(유저)가 사라질 때 이를 참조하는 자식 데이터(게시글, 댓글)가 남아있으면 무결성 제약 조건 위반이 발생합니다. 이를 방지하기 위해 자식 데이터를 먼저 삭제하거나, DB 레벨에서 연쇄 삭제 설정을 하는 것을 말합니다. 이번 프로젝트에서는 Repository 메서드를 통해 명시적으로 순차 삭제를 진행했습니다.

- **Session Sync (세션 동기화)**:
  - **설명**: DB의 정보가 바뀌었더라도 서버 메모리(세션)의 정보가 옛날 것이라면 화면에 반영되지 않습니다. 수정 성공 후 새로운 유저 객체를 세션에 다시 저장(`setAttribute`)하여 즉각적인 동기화를 구현했습니다.
