# 🚩 작업 보고서: 회원가입 아이디 중복 체크 (Step 1)

- **작업 일시**: 2026-03-17
- **진행 단계**: 완료 (Step 1: AJAX 중복 체크 구현)

## 1. 🌊 전체 작업 흐름 (Workflow)
1. **Repository 확장**: `UserRepository`에 `findByUsername` 메서드를 추가하여 DB에서 아이디 존재 여부를 확인할 수 있게 함.
2. **Service 로직 구현**: `UserService`에 `usernameSameCheck` 기능을 만들어, 아이디가 비어있으면(존재하지 않으면) `true`(사용 가능)를 반환하도록 로직 설계.
3. **API 엔드포인트 생성**: `UserApiController`에 `/api/user/username-same-check` 경로를 생성하여 프론트엔드와 통신할 수 있는 창구를 마련.
4. **화면 및 비동기 통신 구현**: `join-form.mustache` 파일을 생성하고, JavaScript의 `fetch` API를 사용하여 사용자가 입력한 아이디의 중복 여부를 실시간으로 서버에 물어보고 화면에 표시하는 로직 구현.

## 2. 🧩 핵심 코드 (Core Logic)

### 🖥️ Backend: UserApiController.java
```java
// 아이디 중복 체크 API
@GetMapping("/api/user/username-same-check")
public ResponseEntity<?> usernameSameCheck(@RequestParam("username") String username) {
    // 사용자가 입력한 username이 DB에 있는지 서비스에 물어봄
    var isAvailable = userService.usernameSameCheck(username);
    // 결과(true/false)를 공통 응답 객체에 담아 반환
    return Resp.ok(isAvailable);
}
```

### 🎨 Frontend: join-form.mustache (JavaScript)
```javascript
// Fetch API를 활용한 비동기 통신
async function checkUsername() {
    var username = document.querySelector("#username").value;
    // 서버의 API를 호출 (페이지 새로고침 없이!)
    var response = await fetch(`/api/user/username-same-check?username=${username}`);
    var result = await response.json();

    if (result.body === true) {
        // 사용 가능한 경우 버튼 활성화 및 메시지 출력
        document.querySelector("#username-msg").innerText = "사용 가능한 아이디입니다.";
        document.querySelector("#btn-join").disabled = false;
    }
}
```

## 3. 🍦 상세비유 (Easy Analogy)
"이번 작업은 **'도서관 회원증 만들기'**와 같습니다. 새로운 사람이 회원가입을 하러 왔을 때, 이미 등록된 이름인지 사서(서버)에게 물어보는 과정이에요. 
1. 신청서(화면)를 쓰다가 '이 이름 되나요?'라고 물어보면(중복확인 클릭),
2. 사서는 장부(DB)를 뒤져서 '안돼요!' 혹은 '돼요!'라고 대답해줍니다(AJAX 응답).
3. '돼요!'라는 대답을 들어야만 최종적으로 가입 버튼을 누를 수 있게 자물쇠를 풀어주는 것과 같습니다."

## 4. 📚 기술 딥다이브 (Technical Deep-dive)
- **AJAX (Asynchronous JavaScript and XML)**: 웹 페이지 전체를 새로고침하지 않고도, 백그라운드에서 서버와 데이터를 주고받는 기술입니다. 이번 작업에서는 `fetch` API가 이 역할을 수행했습니다.
- **비동기(Asynchronous)**: 요청을 보낸 후 응답이 올 때까지 기다리는 동안 다른 일을 할 수 있는 방식입니다. 덕분에 중복 확인을 하는 중에도 사용자는 다른 입력 필드를 작성할 수 있습니다.
- **Optional (Java)**: 데이터가 '있을 수도 있고 없을 수도 있음'을 안전하게 처리하는 자바의 도구입니다. `UserRepository`에서 유저를 찾을 때 사용되었습니다.
