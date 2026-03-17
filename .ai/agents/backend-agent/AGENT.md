---
name: backend-agent
scope: layer
description: >
  Java 도메인 코드 작업 시 자동 발동하는 백엔드 레이어 전문 에이전트.
  Entity, Service, Controller, RestController, Repository, Request/Response DTO 중
  하나 이상을 생성하거나 수정하는 작업이 감지되면 이 에이전트의 규칙이 적용된다.
  키워드: "도메인 만들어줘", "CRUD 구현", "API 만들어줘", "Entity 추가", "Service 짜줘",
  "Repository", "DTO", "컨트롤러", "백엔드", "Java 파일", "엔드포인트"
---

# Backend Agent

## 역할

Spring Boot 3.3.4 프로젝트의 Java 도메인 계층 전문가다.
이 에이전트가 활성화되면 아래의 모든 규칙을 예외 없이 적용한다.

## 활성화 조건 (Auto-detect)

다음 중 하나 이상이 요청에 포함될 때 자동으로 이 에이전트를 적용한다:

- `.java` 파일 생성/수정
- Entity, Service, Controller, Repository, DTO 언급
- API 엔드포인트 구현
- 도메인 기능 CRUD 구현
- 백엔드 로직 작성

## 책임 범위

| 파일 | 위치 |
|------|------|
| `{Domain}.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}Service.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}Controller.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}ApiController.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}Repository.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}Request.java` | `src/main/java/com/example/demo/{domain}/` |
| `{Domain}Response.java` | `src/main/java/com/example/demo/{domain}/` |

## 패키지 구조 규칙

- 베이스 패키지: `com.example.demo`
- 도메인 기반 플랫(flat) 구조. 레이어드 구조 절대 금지.
- 모든 도메인 파일은 `com.example.demo.{domain}` 한 패키지에 위치한다.

## Entity 규칙

### 어노테이션 순서 (순서 엄수)
```java
@NoArgsConstructor
@Data
@Entity
@Table(name = "{domain}_tb")
public class {Domain} {
```

### PK 규칙
- 타입: `Integer` (Long 사용 금지)
- 전략: `GenerationType.IDENTITY`
- `@GeneratedValue`가 `@Id` 위에 위치

```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Id
private Integer id;
```

### Builder 규칙
- `@Builder`는 **생성자에만** 붙인다. 클래스 레벨 금지.
- 컬렉션 필드는 생성자에 포함하지 않는다.

```java
// RULE: 컬렉션은 생성자에 넣지 않는다.
@Builder
public {Domain}(Integer id, String field1, ...) {
    this.id = id;
    this.field1 = field1;
}
```

### 연관관계 규칙
- 모든 연관관계는 `FetchType.LAZY` 필수
- OSIV = false이므로 Service 계층에서 페치 완료 후 DTO 변환

```java
@ManyToOne(fetch = FetchType.LAZY)
private User user;
```

## Service 규칙

### 어노테이션 순서 (순서 엄수)
```java
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class {Domain}Service {
```

### 트랜잭션 규칙
- 클래스 레벨: `@Transactional(readOnly = true)` 기본값
- 쓰기 메서드(save, update, delete): 메서드 레벨에 `@Transactional` 추가

```java
@Transactional
public {Domain}Response.Min save({Domain}Request.Save reqDTO) { ... }
```

### 반환 규칙
- **Entity를 반환하지 않는다**
- 항상 Response DTO inner class를 반환한다

## Controller (SSR) 규칙

### 어노테이션 순서 (순서 엄수)
```java
@RequiredArgsConstructor
@Controller
public class {Domain}Controller {
```

- Mustache 뷰 이름 반환: `return "{domain}/list"`
- 세션 인증: `HttpSession` 직접 사용 (Spring Security 사용 금지)

## RestController 규칙

### 어노테이션 순서 (순서 엄수)
```java
@RequiredArgsConstructor
@RestController
public class {Domain}ApiController {
```

### 응답 규칙
- **모든 REST 응답은 `Resp<T>` 사용 필수**
- 성공: `return Resp.ok(data);`
- 실패: `return Resp.fail(HttpStatus.BAD_REQUEST, "메시지");`
- `Resp` 위치: `com.example.demo._core.utils.Resp`

### URL 규칙
- REST 엔드포인트는 반드시 `/api` 접두사

```java
@PostMapping("/api/{domain}")
public ResponseEntity<?> save(@RequestBody {Domain}Request.Save reqDTO) {
    return Resp.ok(service.save(reqDTO));
}
```

## DTO 규칙

### Request (`{Domain}Request.java`)
```java
public class {Domain}Request {
    // @Data는 inner static class에만
    @Data
    public static class Save { ... }

    @Data
    public static class Update { ... }

    @Data
    public static class Login { ... }   // 로그인

    @Data
    public static class Join { ... }    // 회원가입
}
```

### Response (`{Domain}Response.java`)
```java
public class {Domain}Response {
    @Data
    public static class Max {           // 전체 필드
        public Max({Domain} entity) { ... }
    }

    @Data
    public static class Min {           // 최소 필드 (목록용)
        public Min({Domain} entity) { ... }
    }

    @Data
    public static class Detail {        // 조인 포함 상세
        public Detail({Domain} entity) { ... }
    }

    @Data
    public static class Option {        // 드롭다운용
        private Integer id;
        private String label;
        public Option({Domain} entity) { ... }
    }
}
```

## Repository 규칙

```java
public interface {Domain}Repository extends JpaRepository<{Domain}, Integer> {
    @Query("select b from Board b where b.user.id = :userId")
    List<Board> findByUserId(@Param("userId") Integer userId);
}
```

- PK 타입: `Integer`
- 커스텀 쿼리: `@Query` + JPQL

## 검증 체크리스트

- [ ] 어노테이션 순서가 규칙대로인가 (Entity/Service/Controller)
- [ ] Entity PK 타입이 `Integer`인가
- [ ] `@Builder`가 생성자에만 붙어 있는가
- [ ] 모든 연관관계가 `LAZY`인가
- [ ] Service가 Entity가 아닌 DTO를 반환하는가
- [ ] 쓰기 메서드에 `@Transactional`이 추가되었는가
- [ ] REST 응답이 `Resp.ok()` / `Resp.fail()`을 사용하는가
- [ ] REST URL이 `/api` 접두사를 가지는가
- [ ] DTO의 `@Data`가 inner static class에만 붙어 있는가
- [ ] Repository PK 타입이 `Integer`인가
