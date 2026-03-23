# 서비스 중심 DTO 설계 및 동적 예외 처리

- 날짜: 2026-03-23
- 참여: 사용자 + Gemini CLI

## 배경
컨트롤러에 산재된 페이징 유효성 검사 및 리다이렉트 로직을 서비스로 이관하여 컨트롤러의 가독성을 극대화하고자 함.

## 핵심 논의 및 결론
1. **DTO 완결성**: 서비스는 화면(View)에 필요한 모든 데이터(게시글 목록, 페이징 메타데이터 등)를 하나의 DTO(`model`)로 묶어서 반환한다.
2. **컨트롤러 최소화**: 컨트롤러는 `model.addAttribute("model", dto)`를 단 한 번만 수행하며, 비즈니스 로직이나 유효성 검사 로직을 가지지 않는다.
3. **동적 리다이렉트(B 방향)**: 잘못된 페이지 요청 시 서비스는 예외(예: `Exception400`)를 던지고, `GlobalExceptionHandler`가 HTTP 헤더의 `Referer`를 참조하여 사용자를 이전 페이지로 안전하게 되돌린다.

## 다음 단계
- `code-rule.md` 업데이트.
- `BoardResponse.PagingDTO` 및 `BoardService`, `BoardController` 리팩토링.
- `GlobalExceptionHandler`에 동적 리다이렉트 기능 보강.
