# 🚩 작업 보고서: 게시글 관리 시스템 (CRUD) 및 사용자 중심 페이징 구현

- **작업 일시**: 2026-03-23
- **진행 단계**: 완료

## 1. 🌊 전체 작업 흐름 (Workflow)

```text
+---------------------+      +---------------------+      +---------------------+
|    BoardController  | ---> |     BoardService    | ---> |   BoardRepository   |
| (요청 처리/리다이렉트) | <--- | (비즈니스 로직/DTO)  | <--- |  (DB 데이터 조회)     |
+---------------------+      +---------------------+      +---------------------+
           |                            |
           v                            v
+---------------------+      +---------------------+
|   PagingDTO 생성     |      |   DetailDTO 생성     |
| (현재/전체/이전/다음) |      | (권한/작성자 정보 포함) |
+---------------------+      +---------------------+
           |
           v
+---------------------------------------------------------+
| [화면 구현]                                              |
| 1. list.mustache: 페이징 내비게이션 바 추가                |
| 2. detail.mustache: 상세 보기 및 수정/삭제 버튼            |
| 3. save-form.mustache: 새 글 작성 폼                     |
| 4. update-form.mustache: 기존 글 수정 폼                  |
+---------------------------------------------------------+
```

1. **페이징 메타데이터 설계**: 현재 페이지, 전체 페이지, 이전/다음 활성화 여부를 담는 `PagingDTO` 정의.
2. **서비스 로직 고도화**: `countAll()`을 활용해 전체 페이지 수를 계산하고, 0-based 인덱스로 DB 조회.
3. **컨트롤러 리다이렉션 방어**: 주소창에 잘못된 페이지(0 이하, 최대 초과) 입력 시 유효한 페이지로 강제 이동.
4. **CRUD 기능 완비**: 상세 보기, 작성, 수정, 삭제 기능을 모두 구현하고 세션 유저와 작성자 일치 여부(isOwner) 검증.
5. **UI 구현**: Bootstrap을 활용하여 직관적인 페이징 버튼과 게시판 화면 구성.

## 2. 🧩 변경된 주요 코드

### 1) 페이징 및 상세 DTO (`BoardResponse.java`)
```java
@Data
public static class PagingDTO {
    private List<BoardResponse.ListDTO> boards; // 게시글 목록
    private List<Integer> pageNumbers;          // [1, 2, 3...] 페이지 번호 리스트
    private Integer currentPage;                // 현재 페이지 (1부터 시작)
    private Integer totalPages;                 // 전체 페이지 수
    private boolean first;                      // 첫 페이지 여부 (이전 버튼 비활성화용)
    private boolean last;                       // 마지막 페이지 여부 (다음 버튼 비활성화용)

    public PagingDTO(List<BoardResponse.ListDTO> boards, int currentPage, int totalPages) {
        this.boards = boards;
        this.currentPage = currentPage + 1;     // 내부 0 -> 외부 1 변환
        this.totalPages = totalPages;
        // ... 생략 (이전/다음 페이지 계산 로직)
    }
}
```

### 2) 컨트롤러 리다이렉션 로직 (`BoardController.java`)
```java
@GetMapping("/")
public String list(@RequestParam(defaultValue = "1", name = "page") int page, Model model) {
    // 1. 하한선 방어: 1페이지보다 작은 값이 오면 1로 보내버림
    if (page < 1) return "redirect:/?page=1";

    BoardResponse.PagingDTO pagingDTO = boardService.게시글목록보기(page - 1);
    
    // 2. 상한선 방어: 전체 페이지보다 큰 값이 오면 마지막 페이지로 보내버림
    if (page > pagingDTO.getTotalPages() && pagingDTO.getTotalPages() > 0) {
        return "redirect:/?page=" + pagingDTO.getTotalPages();
    }

    model.addAttribute("model", pagingDTO);
    return "board/list";
}
```

## 3. 🍦 상세비유 (Easy Analogy)

"이번 작업은 **'친절한 도서관 사서'**와 같습니다!"

마치 도서관에서 책을 찾을 때:
1. **페이징**: "1000권의 책을 한꺼번에 보여주면 어지러우니, 한 번에 3권씩만 보여주는 앨범"을 만든 것과 같습니다.
2. **리다이렉션**: 손님이 "0페이지 보여주세요!" 하거나 "없는 999페이지 보여주세요!"라고 억지를 부려도, 사서가 웃으며 **"손님, 1페이지부터 시작입니다."** 또는 **"마지막은 10페이지입니다."**라며 올바른 페이지를 펼쳐주는 것과 같습니다.
3. **CRUD/권한**: 본인이 쓴 일기장(게시글)은 본인만 고치거나 찢을(삭제) 수 있도록 신분증(세션)을 확인하는 절차를 만든 것입니다.

## 4. 📚 기술 딥다이브 (Technical Deep-dive)

- **[Paging Logic]**: 사용자 경험(UX)을 위해 URL은 1부터 시작하게 만들었지만, 실제 DB(JPA/SQL)는 0부터 시작하는 `OFFSET`을 사용합니다. 이를 컨트롤러에서 `page - 1`로 보정하여 처리했습니다.
- **[Redirection Strategy]**: 서버 내부에서 데이터를 단순히 보정해서 보여주는 대신 `redirect`를 사용했습니다. 이는 사용자의 브라우저 주소창을 올바른 주소로 갱신시켜주어, 사용자가 현재 자신이 어디에 있는지 명확히 알게 하고 즐겨찾기 등을 할 때도 오류가 없게 합니다.
- **[IsOwner Check]**: `DetailDTO` 생성 시점에 세션 유저의 ID와 게시글 작성자의 ID를 비교하여 `isOwner` 불리언 값을 담았습니다. 이를 통해 Mustache 템플릿에서 `{{#isOwner}}` 조건문 하나만으로 수정/삭제 버튼의 노출 여부를 안전하게 제어할 수 있습니다.
