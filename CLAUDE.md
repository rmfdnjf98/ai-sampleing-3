## 1. 자동 동기화 (최우선 실행)

이 문서를 읽는 즉시 `.ai/skills/*/SKILL.md` 및 `.ai/agents/*/AGENT.md` 파일을 스캔하여 description을 반드시 읽어라.

---

## 2. 참조 규칙 (Memory Load)
아래 파일을 읽고 규칙을 반드시 준수한다.

- **컨벤션**: `.ai/rules/*.md`

---

## 3. 활동 로깅 (Activity Logging)

`.ai/hooks/log-activity.sh`를 통해 `.person/logs/activity.log`에 기록된다.
커스텀 스킬의 SKILL.md를 읽어 실행할 때, **반드시** 아래 명령을 함께 실행하여 로그를 남겨라:
```bash
echo '{"tool_name":"Skill","tool_input":{"skill":"<스킬명>"}}' | bash .ai/hooks/log-activity.sh
```
---

## 4. AI-CONTEXT
코드 작업 전 해당 디렉토리에 `AI-CONTEXT.md`가 있으면 먼저 읽어라.

---

## 5. 에이전트 자동 선택 및 병렬 적용 규칙

작업 요청을 받으면 즉시 관련 에이전트를 판별하여 해당 규칙을 적용한다.

### 에이전트 선택 기준

| 작업 유형 | 적용 에이전트 |
|-----------|--------------|
| `.java` 파일 생성/수정 (Entity, Service, Controller, Repository, DTO) | `backend-agent` |
| `.mustache` 파일 생성/수정, HTML/JS/CSS 작업 | `frontend-agent` |
| 풀스택 도메인 기능 구현 (Java + Mustache 동시) | **양쪽 동시 적용** |

### 병렬 적용 방식

풀스택 작업 시 두 에이전트를 동시에 활성화한다:
- **backend-agent**: 모든 `.java` 파일 작업 시 `.ai/agents/backend-agent/AGENT.md` 규칙 적용
- **frontend-agent**: 모든 `.mustache`, `.js`, `.css` 파일 작업 시 `.ai/agents/frontend-agent/AGENT.md` 규칙 적용
- 두 에이전트의 책임 범위는 겹치지 않으므로 독립적으로 동작한다

### 판별 예시

- `"user 도메인 CRUD 만들어줘"` → backend-agent + frontend-agent 동시 적용
- `"Board Entity에 필드 추가해줘"` → backend-agent만 적용
- `"로그인 폼 화면 만들어줘"` → frontend-agent만 적용
- `"게시글 목록 API랑 화면 같이 만들어줘"` → backend-agent + frontend-agent 동시 적용