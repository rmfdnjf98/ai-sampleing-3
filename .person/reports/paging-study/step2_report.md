# 🚩 작업 보고서: [Step 2] SQL 기초 페이징 (LIMIT/OFFSET) 구현

- **작업 일시**: 2026-03-20
- **진행 단계**: 완료 ✅

## 1. 🌊 전체 작업 흐름 (Workflow)

```text
[사용자 요청] -> [Controller] -> [Service] -> [Repository] -> [DB]
    |             |              |               |           |
    |  /?page=1   |  page=1 수신   | offset=3 계산 | JPQL 실행   | 3개만 추출
    +------------>+-------------->+-------------->+---------->+
                                                              |
    <------------+<--------------+<--------------+<----------+
      HTML 응답    |   DTO 변환    |   3개 데이터   |   Result
```

1. **사용자 요청**: 브라우저 주소창에 `/?page=n` 파라미터를 입력하여 특정 페이지를 요청합니다.
2. **Controller 수신**: `@RequestParam`을 통해 `page` 번호를 안전하게 받아옵니다. (기본값 0)
3. **Service 계산**: 한 페이지당 보여줄 개수(limit=3)와 건너뛸 개수(offset = page * 3)를 계산합니다.
4. **Repository 조회**: JPQL의 `LIMIT`, `OFFSET` 구문을 사용하여 DB에서 정확히 필요한 3개의 데이터만 가져옵니다.
5. **화면 출력**: 가져온 3개의 게시글과 현재 페이지 번호를 Mustache 템플릿에 뿌려줍니다.

## 2. 🧩 변경된 핵심 코드

### ① Repository (데이터 관문)
JPQL 문법을 사용하여 객체 지향적으로 데이터를 끊어옵니다.
```java
@Query("SELECT b FROM Board b ORDER BY b.id DESC LIMIT :limit OFFSET :offset")
List<Board> findAll(@Param("limit") int limit, @Param("offset") int offset);
```

### ② Service (비즈니스 로직 - 핵심 계산기)
사용자가 원하는 페이지를 DB가 알아듣는 '건너뛰기(Offset)' 숫자로 변환합니다.
```java
public List<BoardResponse.ListDTO> 게시글목록보기(int page) {
    int limit = 3; // 한 번에 3개씩만!
    int offset = page * limit; // 0페이지는 0개, 1페이지는 3개, 2페이지는 6개 스킵!

    List<Board> boards = boardRepository.findAll(limit, offset);
    return boards.stream().map(BoardResponse.ListDTO::new).collect(Collectors.toList());
}
```

### ③ Controller (요청 배달원)
사용자의 목소리(URL 파라미터)를 들어 서비스에 전달합니다.
```java
@GetMapping("/")
public String list(@RequestParam(defaultValue = "0") int page, Model model) {
    var data = boardService.게시글목록보기(page);
    model.addAttribute("models", data); // 3개의 데이터만 담김
    model.addAttribute("currentPage", page); // 현재 위치 표시용
    return "board/list";
}
```

## 3. 🍦 상세비유: 도서관 사서와 책장

이번 작업은 **"거대한 도서관에서 원하는 책 3권만 딱 집어오는 것"**과 같습니다.

- **도서관 전체 책장**: DB에 쌓인 수많은 게시글들 (23개)
- **사서 (Service)**: "1페이지 주세요"라는 말을 듣고, "아, 처음 3권은 건너뛰고(Offset) 그 다음 3권(Limit)을 가져와야겠군!" 하고 계산하는 역할입니다.
- **LIMIT (바구니 크기)**: 한 번에 담아올 수 있는 바구니의 크기가 3개라는 뜻입니다.
- **OFFSET (스킵하기)**: 앞에서부터 몇 권의 책을 무시하고 지나칠지를 결정합니다.

마치 두꺼운 전집 중 **"2권(page=1)을 보여줘"**라고 하면, **앞의 1권(offset=3)을 지나치고 그 다음 3권(limit=3)을 꺼내오는 것**과 같습니다!

## 4. 📚 기술 딥다이브 (Technical Deep-dive)

- **JPQL LIMIT/OFFSET**: 
  원래 JPQL 표준에는 `LIMIT` 키워드가 없었으나, 하이버네이트 6(Spring Boot 3.x)부터는 SQL처럼 직접 쿼리문에 작성할 수 있게 되었습니다. 덕분에 `nativeQuery = true` 없이도 DB 엔진의 페이징 기능을 직접 제어할 수 있습니다.
  
- **Offset 계산 공식 (`page * size`)**: 
  컴퓨터는 0부터 숫자를 세기 때문에(Zero-based), 0페이지가 시작입니다. 
  - `0 * 3 = 0` (0개 건너뜀 = 1등부터 가져옴)
  - `1 * 3 = 3` (3개 건너뜀 = 4등부터 가져옴)
  이 간단한 곱셈이 페이징의 가장 밑바닥 원리입니다.
