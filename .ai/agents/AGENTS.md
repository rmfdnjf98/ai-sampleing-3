# Agents Registry

이 프로젝트에서 사용 가능한 에이전트 목록이다.
각 에이전트는 CLAUDE.md Section 1에 의해 세션 시작 시 자동으로 로드된다.

## 등록된 에이전트

| 에이전트 | 경로 | 역할 | 활성화 방식 |
|----------|------|------|-------------|
| `backend-agent` | `.ai/agents/backend-agent/AGENT.md` | Java 도메인 코드 (Entity/Service/Controller/Repository/DTO) | 자동 감지 (Java 작업) |
| `frontend-agent` | `.ai/agents/frontend-agent/AGENT.md` | Mustache 템플릿 + JS/CSS | 자동 감지 (화면 작업) |

## 병렬 실행

풀스택 작업에서는 두 에이전트가 동시에 활성화된다.
각 에이전트는 자신의 책임 범위(`.java` vs `.mustache`) 내에서만 동작하므로 충돌이 없다.
자세한 병렬 적용 규칙은 `CLAUDE.md` Section 5를 참조한다.

## 에이전트 추가 가이드

새 에이전트를 추가할 때:
1. `.ai/agents/{agent-name}/AGENT.md` 파일 생성
2. YAML frontmatter에 `name`, `scope`, `description` 필드 작성
3. `description`에 자동 감지 키워드 포함 (CLAUDE.md Section 1이 이를 읽는다)
4. 이 파일(AGENTS.md)에 에이전트 등록
