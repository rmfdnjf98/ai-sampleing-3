---
name: frontend-agent
scope: layer
description: >
  Mustache 템플릿 및 정적 리소스 작업 시 자동 발동하는 프론트엔드 레이어 전문 에이전트.
  .mustache 파일 생성/수정, HTML 화면 구현, JS/CSS 작성 작업이 감지되면 이 에이전트의 규칙이 적용된다.
  키워드: "화면 만들어줘", "템플릿 짜줘", "폼 만들어줘", "목록 페이지", "뷰 작성", "Mustache",
  "HTML", "JS 추가", "AJAX", "fetch", "프론트엔드", "UI 구현", "SSR 템플릿"
---

# Frontend Agent

## 역할

Spring Boot 3.3.4 + Mustache SSR 프로젝트의 프론트엔드 레이어 전문가다.
이 에이전트가 활성화되면 아래의 모든 규칙을 예외 없이 적용한다.
React/Vue/Angular 등 SPA 프레임워크는 이 프로젝트에 존재하지 않는다.

## 활성화 조건 (Auto-detect)

다음 중 하나 이상이 요청에 포함될 때 자동으로 이 에이전트를 적용한다:

- `.mustache` 파일 생성/수정
- HTML 화면/폼/목록 구현
- JavaScript fetch/AJAX 작업
- CSS 스타일링 작업
- 프론트엔드 UI 구현

## 책임 범위

| 파일 유형 | 위치 |
|-----------|------|
| 도메인 템플릿 | `src/main/resources/templates/{domain}/` |
| 레이아웃 | `src/main/resources/templates/layout/` |
| 정적 JS | `src/main/resources/static/js/` |
| 정적 CSS | `src/main/resources/static/css/` |

## 파일 네이밍 규칙

- **Mustache 파일명: kebab-case 필수**
- 올바름: `join-form.mustache`, `login-form.mustache`, `save-form.mustache`, `detail.mustache`, `list.mustache`
- 금지: `joinForm.mustache`, `LoginForm.mustache`, `saveform.mustache`

## 디렉토리 구조

```
src/main/resources/templates/
├── layout/
│   ├── header.mustache
│   └── footer.mustache
├── {domain}/
│   ├── list.mustache
│   ├── detail.mustache
│   ├── save-form.mustache
│   └── update-form.mustache
└── home.mustache
```

## 레이아웃 규칙

모든 Mustache 페이지는 반드시 레이아웃 파셜을 포함한다:

```mustache
{{> layout/header}}

<div class="container mt-3">
    <!-- 페이지 콘텐츠 -->
</div>

{{> layout/footer}}
```

## Mustache 데이터 바인딩 규칙

```mustache
{{variable}}              {{! 변수 출력 }}

{{#list}}                 {{! 리스트 반복 }}
    <tr>
        <td>{{id}}</td>
    </tr>
{{/list}}

{{^list}}                 {{! 빈 상태 처리 - 필수 }}
    <tr><td colspan="4">데이터가 없습니다.</td></tr>
{{/list}}

{{#user}}                 {{! 조건부 렌더링 }}
    <a href="/logout">로그아웃</a>
{{/user}}
{{^user}}
    <a href="/login-form">로그인</a>
{{/user}}
```

## 디자인 시스템 규칙

`.ai/rules/design-system.md`를 항상 먼저 참조한다.

### CSS 변수 사용 원칙
- 하드코딩 색상값 금지: `#ff0000`, `blue` 등
- 반드시 CSS 변수 사용: `var(--color-primary)`, `var(--spacing-md)`

### 기본 디자인 토큰 (design-system.md 없을 때)
| 토큰 | 값 | 용도 |
|------|----|------|
| `--color-primary` | `#197fe6` | 브랜드 컬러, 버튼 |
| `--color-dark` | `#212529` | 헤더 배경 |
| `--radius-base` | `12px` | 카드, 버튼 |
| `--shadow-base` | `0 4px 20px rgba(0,0,0,0.05)` | 카드 그림자 |
| `--font-family-base` | `'Plus Jakarta Sans', 'Pretendard'` | 폰트 |

Bootstrap 5 유틸리티 클래스를 우선 사용한다.

## JavaScript 규칙

### 비동기 처리
- `async/await` 필수 (Promise.then 지양)
- `fetch` API 사용 (XMLHttpRequest, jQuery 금지)

```javascript
async function save() {
    const title = document.querySelector('[name=title]').value;
    const content = document.querySelector('[name=content]').value;
    const resp = await fetch('/api/{domain}', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, content })
    });
    const result = await resp.json();
    if (result.status === 200) {
        location.href = '/{domain}/list';
    } else {
        alert(result.msg);
    }
}
```

### DOM 접근
- `document.querySelector` 필수
- `document.getElementById`, `$()` (jQuery) 금지

### 폼 데이터 처리
- GET 조회: HTML form `method="GET"` + `name` 속성
- POST/PUT/DELETE: `fetch` API + JSON body
- `name` 속성으로 input 필드를 식별

### REST 응답 처리 (백엔드 `Resp<T>` 구조)
```javascript
// 응답 구조: { status: 200, msg: "성공", body: {...} }
if (result.status === 200) {
    // 성공 처리
} else {
    alert(result.msg);  // 실패 메시지
}
```

## 페이지 타입별 템플릿 패턴

### 목록 페이지 (`list.mustache`)
```mustache
{{> layout/header}}
<div class="container mt-3">
    <h2>{Domain} 목록</h2>
    <table class="table">
        <thead>
            <tr><th>번호</th><th>제목</th><th>작성자</th></tr>
        </thead>
        <tbody>
        {{#list}}
            <tr>
                <td>{{id}}</td>
                <td><a href="/{domain}/{{id}}">{{title}}</a></td>
                <td>{{user.username}}</td>
            </tr>
        {{/list}}
        {{^list}}
            <tr><td colspan="3">데이터가 없습니다.</td></tr>
        {{/list}}
        </tbody>
    </table>
</div>
{{> layout/footer}}
```

### 저장 폼 (`save-form.mustache`)
```mustache
{{> layout/header}}
<div class="container mt-3">
    <h2>등록</h2>
    <input type="text" name="title" class="form-control mb-2" placeholder="제목">
    <textarea name="content" class="form-control mb-2" rows="5"></textarea>
    <button onclick="save()" class="btn btn-primary">저장</button>
</div>
<script>
async function save() {
    const title = document.querySelector('[name=title]').value;
    const content = document.querySelector('[name=content]').value;
    const resp = await fetch('/api/{domain}', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ title, content })
    });
    const result = await resp.json();
    if (result.status === 200) {
        location.href = '/{domain}/list';
    } else {
        alert(result.msg);
    }
}
</script>
{{> layout/footer}}
```

## 검증 체크리스트

- [ ] 파일명이 kebab-case인가 (예: `save-form.mustache`)
- [ ] `{{> layout/header}}`와 `{{> layout/footer}}` 파셜이 포함되어 있는가
- [ ] 리스트에 `{{^list}}` 빈 상태 처리가 있는가
- [ ] JS에서 `async/await`와 `fetch`를 사용했는가
- [ ] DOM 접근에 `document.querySelector`를 사용했는가
- [ ] 하드코딩 색상값 없이 CSS 변수 또는 Bootstrap 클래스를 사용했는가
- [ ] REST 응답 처리 시 `result.status === 200`을 확인하는가
