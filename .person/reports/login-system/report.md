# 🚩 작업 보고서: T-2.2 로그인/로그아웃 기능 및 화면 구현

- **작업 일시**: 2026-03-19
- **진행 단계**: 완료

## 1. 🌊 전체 작업 흐름 (Workflow)

```text
+---------------------+       +-----------------------+       +-------------------------+
| [1] 로그인 폼 (View)  | ----> | [2] 로그인 요청 (POST) | ----> | [3] 서비스 로직 (Auth)   |
| (username, password)|       | (/login)              |       | (ID/PW 검증, BCrypt)     |
+---------------------+       +-----------------------+       +-------------------------+
                                                                         |
          +--------------------------------------------------------------+
          |
          v
+-----------------------+       +-----------------------+       +-------------------------+
| [4] 세션 저장 (Session)| ----> | [5] 홈으로 리다이렉트  | ----> | [6] 헤더 메뉴 동적 변경   |
| (sessionUser)         |       | (redirect:/)          |       | (로그아웃/정보수정 노출)  |
+-----------------------+       +-----------------------+       +-------------------------+
```

1. **DTO 유효성 검사 추가**: `UserRequest.Login`에 `@NotBlank`를 적용하여 빈 값이 들어오지 못하게 차단했습니다.
2. **비즈니스 로직 구현**: `UserService`에서 DB에 저장된 암호화된 비밀번호와 입력된 비밀번호를 `BCrypt`로 비교하는 로직을 구현했습니다.
3. **컨트롤러 엔드포인트**: 로그인 성공 시 세션에 유저 정보를 저장하고, 로그아웃 시 세션을 무효화(`invalidate`)하도록 설정했습니다.
4. **화면 구현**: Bootstrap을 사용하여 깔끔한 로그인 폼을 만들고, Mustache의 `{{#sessionUser}}` 문법으로 로그인 상태에 따른 헤더 메뉴를 분기했습니다.

## 2. 🧩 변경된 모든 코드 포함

### 1) DTO (UserRequest.java)
```java
public static class Login {
    @NotBlank(message = "유저네임은 필수입니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}
```

### 2) Service (UserService.java)
```java
public User login(UserRequest.Login reqDTO) {
    // 1. 유저네임으로 DB에서 사용자 찾기
    var userOp = userRepository.findByUsername(reqDTO.getUsername());
    if (userOp.isEmpty()) {
        throw new Exception400("아이디 또는 비밀번호가 틀렸습니다.");
    }

    User user = userOp.get();

    // 2. 암호화된 비밀번호와 입력된 비밀번호 매칭 확인
    if (!passwordEncoder.matches(reqDTO.getPassword(), user.getPassword())) {
        throw new Exception400("아이디 또는 비밀번호가 틀렸습니다.");
    }

    return user;
}
```

### 3) Controller (UserController.java)
```java
@PostMapping("/login")
public String login(@Valid UserRequest.Login reqDTO, BindingResult bindingResult) {
    // 서비스에서 인증된 유저 객체를 받아옴
    User user = userService.login(reqDTO);
    // 세션이라는 '금고'에 유저 정보를 보관
    session.setAttribute("sessionUser", user);
    return "redirect:/";
}

@GetMapping("/logout")
public String logout() {
    // 금고의 열쇠를 파기하고 비움
    session.invalidate();
    return "redirect:/";
}
```

### 4) View (header.mustache)
```html
<ul class="navbar-nav">
    {{#sessionUser}} <!-- 로그인 했을 때만 보임 -->
        <li class="nav-item"><a class="nav-link" href="/logout">로그아웃</a></li>
    {{/sessionUser}}
    {{^sessionUser}} <!-- 로그인 안 했을 때만 보임 -->
        <li class="nav-item"><a class="nav-link" href="/login-form">로그인</a></li>
    {{/sessionUser}}
</ul>
```

### 5) Dummy Data (data.sql)
테스트를 위해 `ssar` 유저의 비밀번호를 "1234"의 BCrypt 해시값으로 설정하였습니다.
- **아이디**: `ssar`
- **평문 비밀번호**: `1234`
- **해시 비밀번호**: `$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyAY2En5g6nm7CeWz7JwWWQy3Sz.`

## 3. 🍦 상세비유 쉬운 예시 (Easy Analogy)

"이번 작업은 **놀이공원 자유이용권**과 같습니다. 

로그인은 놀이공원 매표소에서 **본인 확인(ID/PW)**을 하고 **손목 밴드(세션)**를 차는 과정입니다. 한 번 손목 밴드를 차면 매번 입구에서 신분증을 보여줄 필요 없이 놀이기구를 탈 수 있죠. **로그아웃**은 공원을 나가면서 손목 밴드를 끊어버리는 것과 같습니다. 밴드가 없으면 더 이상 놀이기구(회원 전용 기능)를 이용할 수 없게 됩니다!"

## 4. 📚 기술 딥다이브 (Technical Deep-dive)

- **BCrypt Password Encoding**: 
  - **설명**: 비밀번호를 평문으로 저장하지 않고 복호화가 불가능한 해시값으로 저장합니다. 로그인 시에는 입력값과 저장된 해시값을 비교(`matches`)하여 인증합니다.
  - **코드**: `passwordEncoder.matches(raw, encoded)`
  
- **HttpSession**: 
  - **설명**: 클라이언트(브라우저)별로 서버 메모리에 독립적인 저장 공간을 할당합니다. 브라우저가 종료되거나 세션이 만료될 때까지 데이터를 유지하여 '상태가 없는(Stateless)' HTTP 프로토콜에서 인증 상태를 유지하게 해줍니다.
  - **코드**: `session.setAttribute("key", value)`

- **Mustache Sections (`#`, `^`)**:
  - **설명**: 머스테치 템플릿 엔진에서 조건부 렌더링을 담당합니다. `#`은 데이터가 존재할 때(True), `^`는 데이터가 없을 때(False/Null) 실행됩니다.
  - **코드**: `{{#sessionUser}} ... {{/sessionUser}}`
