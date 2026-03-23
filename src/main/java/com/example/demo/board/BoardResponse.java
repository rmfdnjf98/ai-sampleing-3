package com.example.demo.board;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.user.User;

import lombok.Data;

public class BoardResponse {

    // 1. 목록 페이지 전체 데이터 (블록 페이징 적용)
    @Data
    public static class ListDTO {
        private List<BoardDTO> boards; // 게시글 리스트
        private Integer page;          // 현재 페이지 (1부터 시작)
        private Integer prevPage;      // 이전 페이지 번호
        private Integer nextPage;      // 다음 페이지 번호
        private List<Integer> pageNumbers; // 현재 블록의 페이지 번호 목록 [1, 2, 3, 4, 5]
        private boolean first;         // 첫 페이지 여부 (이전 버튼 비활성화용)
        private boolean last;          // 마지막 페이지 여부 (다음 버튼 비활성화용)
        
        // 블록 페이징 추가 필드
        private Integer startPage;     // 블록의 시작 번호
        private Integer endPage;       // 블록의 끝 번호

        public ListDTO(List<BoardDTO> boards, int currentPage, int totalPages) {
            this.boards = boards;
            this.page = currentPage + 1; // 내부 인덱스 0 -> 외부 페이지 1
            this.prevPage = this.page - 1;
            this.nextPage = this.page + 1;
            this.first = (this.page == 1);
            this.last = (this.page == totalPages || totalPages == 0);
            
            // [Step 4] 블록 페이징 로직 (blockSize = 5)
            // 1. 블록 사이즈 설정: 한 화면에 보여줄 번호 개수
            int blockSize = 5;

            // 2. 현재 블록의 시작 페이지 번호 계산
            // 공식: ((현재페이지 - 1) / 블록사이즈) * 블록사이즈 + 1
            // 예: 현재 3페이지면 (2/5)*5 + 1 = 1, 현재 7페이지면 (6/5)*5 + 1 = 6
            this.startPage = ((this.page - 1) / blockSize) * blockSize + 1;

            // 3. 현재 블록의 끝 페이지 번호 계산 (Math.min 활용)
            // 질문 답변: "유령 페이지 방지용 안전장치"
            // 기계적으로는 startPage + 4를 보여줘야 하지만, 실제 데이터(totalPages)가 8개뿐이라면 
            // 9, 10번은 노출하지 않고 8번에서 멈추도록 작은 쪽(Math.min)을 선택함.
            this.endPage = Math.min(this.startPage + blockSize - 1, totalPages);
            
            // 4. 화면에 뿌려줄 번호 리스트 생성
            this.pageNumbers = new ArrayList<>();
            for (int i = this.startPage; i <= this.endPage; i++) {
                pageNumbers.add(i);
            }
        }
    }

    // 2. 게시글 한 줄 데이터
    @Data
    public static class BoardDTO {
        private Integer id;
        private String title;
        private String username;

        public BoardDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.username = board.getUser().getUsername();
        }
    }

    @Data
    public static class DetailDTO {
        private Integer id;
        private String title;
        private String content;
        private Integer userId;
        private String username;
        private boolean isOwner;

        public DetailDTO(Board board, User sessionUser) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.userId = board.getUser().getId();
            this.username = board.getUser().getUsername();
            this.isOwner = (sessionUser != null && sessionUser.getId().equals(this.userId));
        }
    }
}
