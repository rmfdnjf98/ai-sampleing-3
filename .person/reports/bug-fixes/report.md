# 🚩 작업 보고서: 시스템 버그 수정 및 안정화 (2026-03-19)

- **작업 일시**: 2026-03-19
- **상태**: 완료 (시스템 정상 작동 확인)

## 1. 🌊 주요 이슈 및 해결 흐름 (Bug Fix Workflow)

```text
[URL 지저분함] ----> [jsessionid 제거] ----> URL 깔끔 (쿠키 강제)
[화면 렌더링 에러] --> [Model & DTO 도입] --> Mustache 에러 해결
[탈퇴 시 하얀 화면] --> [연쇄 삭제 보안] ----> 데이터 무결성 확보
[로그인 메시지] ----> [메시지 세분화] ----> 사용자 피드백 개선
```

## 2. 🧩 해결된 주요 코드

### 1) URL에서 `;jsessionid` 제거 (application.properties)
- **현상**: 로그인 후 URL 뒤에 긴 세션 ID가 붙어 정적 리소스를 찾지 못하는 오류 발생.
- **해결**: 세션 추적을 '쿠키'로만 하도록 설정하여 URL 재작성 기능을 껐습니다.
```properties
server.servlet.session.tracking-modes=cookie
```

### 2) Mustache 'No key' 에러 해결 (UserController & DTO)
- **현상**: `sessionUser.email` 접근 시 데이터가 `null`이면 머스테치가 예외를 던지며 화면을 그리지 못함.
- **해결**: 컨트롤러에서 `Model`에 직접 DTO를 담아 보내고, DTO 생성자에서 `null`을 빈 문자열(`""`)로 치환했습니다.
```java
// UserResponse.Detail DTO 내부
public Detail(User user) {
    this.email = user.getEmail() != null ? user.getEmail() : ""; // null 방어 코드
}

// UserController 내부
model.addAttribute("user", new UserResponse.Detail(sessionUser));
```

### 3) 탈퇴 시 외래키 제약 조건 해결 (UserService & ReplyRepository)
- **현상**: 탈퇴 시 "내가 쓴 글에 달린 다른 사람의 댓글" 때문에 DB 삭제가 거부되어 하얀 화면이 뜸.
- **해결**: `ReplyRepository`에 게시글 ID 기반 삭제 쿼리를 추가하여 모든 자식 데이터를 먼저 지우도록 순서를 조정했습니다.
```java
@Modifying
@Query("delete from Reply r where r.board.id in (select b.id from Board b where b.user.id = :userId)")
void deleteByBoardUserId(@Param("userId") Integer userId);
```

### 4) 로그인 피드백 개선 (UserService)
- **현상**: 아이디가 없는 건지 비밀번호가 틀린 건지 알 수 없는 모호한 메시지.
- **해결**: 로직을 분리하여 사용자에게 정확한 실패 원인을 전달합니다.
```java
if (userOp.isEmpty()) throw new Exception400("존재하지 않는 회원입니다.");
if (!passwordEncoder.matches(...)) throw new Exception400("비밀번호가 틀렸습니다.");
```

## 3. 🍦 상세비유 쉬운 예시 (Easy Analogy)

"오늘 작업은 **완공된 건물의 인테리어를 다듬고 하자를 보수하는 것**과 같습니다."

- **jsessionid 제거**는 집 주소 끝에 붙은 불필요한 번지수를 떼고 도로명 주소로 깔끔하게 정리한 것입니다.
- **Mustache 에러 해결**은 택배 상자(객체)를 열었을 때 내용물(필드)이 없어도 당황하지 않고 '빈 상자'라고 알려주어 작업을 멈추지 않게 한 것입니다.
- **탈퇴 로직 보완**은 건물을 철거할 때 그 안에 남은 짐들(댓글)을 먼저 싹 비워야 건물이 안전하게 무너지는 것과 같은 원리입니다!

## 4. 📚 기술 딥다이브 (Technical Deep-dive)

- **Session Tracking Mode**: HTTP는 원래 상태가 없지만, 서버는 쿠키나 URL에 ID를 심어 사용자를 식별합니다. 보안과 미관을 위해 쿠키 모드를 권장합니다.
- **Referential Integrity (참조 무결성)**: DB의 외래키는 데이터 간의 관계를 보장합니다. 부모를 지우기 전 반드시 자식을 먼저 지워야 데이터가 꼬이지 않습니다.
- **Model vs Session**: 뷰 엔진(Mustache)은 `Model`에 직접 담긴 데이터를 가장 빠르고 정확하게 찾아냅니다. 세션 속성 노출은 편리하지만 엄격한 제약이 따릅니다.
