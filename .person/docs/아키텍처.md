# 아키텍처 설계서: 심플 다중 사용자 블로그

## 1. 기술 스택 (Tech Stack)
- **Backend**: Java 21, Spring Boot 3.3.4
- **ORM**: Spring Data JPA (Hibernate)
- **Database**: H2 (In-memory)
- **View Engine**: Mustache (SSR)
- **Authentication**: HttpSession (Session-based)
- **Async Communication**: Fetch API (for AJAX requests)

## 2. ERD 설계 (Entity Relationship Diagram)

### 2.1 User (user_tb)
- `id`: Integer (Primary Key, Identity)
- `username`: String (Unique, Not Null) - 아이디
- `password`: String (Not Null) - 비밀번호
- `email`: String (Not Null) - 이메일
- `createdAt`: LocalDateTime (Not Null) - 가입일시

### 2.2 Board (board_tb)
- `id`: Integer (Primary Key, Identity)
- `title`: String (Not Null) - 글 제목
- `content`: String (Not Null) - 글 내용 (텍스트 전용)
- `userId`: Integer (Foreign Key -> User.id, On Delete Set Null) - 작성자 (탈퇴 시 null)
- `createdAt`: LocalDateTime (Not Null) - 작성일시

### 2.3 Reply (reply_tb)
- `id`: Integer (Primary Key, Identity)
- `comment`: String (Not Null) - 댓글 내용
- `userId`: Integer (Foreign Key -> User.id, On Delete Set Null) - 작성자 (탈퇴 시 null)
- `boardId`: Integer (Foreign Key -> Board.id, On Delete Cascade) - 게시글 (게시글 삭제 시에는 댓글도 삭제)
- `createdAt`: LocalDateTime (Not Null) - 작성일시

## 3. API 명세서 (API Specification)

### 3.1 회원 관리 (User)
| Method | URL | Description | View / Response |
|--------|-----|-------------|-----------------|
| GET | `/join-form` | 회원가입 페이지 | `user/join-form` |
| POST | `/join` | 회원가입 요청 | Redirect `/login-form` |
| GET | `/login-form` | 로그인 페이지 | `user/login-form` |
| POST | `/login` | 로그인 요청 | Redirect `/` |
| POST | `/logout` | 로그아웃 요청 | Redirect `/` |
| GET | `/user/update-form` | 회원정보 수정 페이지 | `user/update-form` |
| POST | `/user/update` | 회원정보 및 비밀번호 수정 | Redirect `/` |
| POST | `/user/withdraw` | 회원 탈퇴 요청 (연관 데이터 null 처리) | Redirect `/` |
| GET | `/api/username-check` | 아이디 중복 체크 (AJAX) | `Resp.ok()` |

### 3.2 게시글 관리 (Board)
| Method | URL | Description | View / Response |
|--------|-----|-------------|-----------------|
| GET | `/` | 메인 피드 (목록/페이징/검색/탈퇴회원글 제외) | `board/main` |
| GET | `/board/save-form` | 게시글 작성 페이지 | `board/save-form` |
| POST | `/board/save` | 게시글 작성 요청 | Redirect `/` |
| GET | `/board/{id}` | 게시글 상세 페이지 | `board/detail` |
| GET | `/board/{id}/update-form` | 게시글 수정 페이지 | `board/update-form` |
| POST | `/board/{id}/update` | 게시글 수정 요청 | Redirect `/board/{id}` |
| POST | `/board/{id}/delete` | 게시글 삭제 요청 (연관 댓글 삭제) | Redirect `/` |

### 3.3 댓글 관리 (Reply)
| Method | URL | Description | View / Response |
|--------|-----|-------------|-----------------|
| POST | `/reply/save` | 댓글 작성 요청 | Redirect `/board/{boardId}` |
| POST | `/reply/{id}/delete` | 댓글 삭제 요청 | Redirect `/board/{boardId}` |

## 4. 데이터베이스 제약 사항
- **User 탈퇴 시**: `board_tb`와 `reply_tb`의 `userId`를 `SET NULL` 처리한다.
- **Board 삭제 시**: 해당 게시글에 달린 `reply_tb`의 데이터는 `ON DELETE CASCADE`로 함께 삭제한다.
- `username` 필드에는 `UNIQUE` 제약 조건을 부여하여 아이디 중복을 방지한다.
