package com.example.demo.board;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * DTO는 Service에서 만든다. Entity를 Controller에 전달하지 않는다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public List<BoardResponse.ListDTO> 게시글목록보기(int page) {
        int limit = 3; // 한 페이지당 보여줄 개수
        int offset = page * limit; // 0페이지는 0개 스킵, 1페이지는 3개 스킵 -> 시작 인덱스!!

        // JPQL로 작성된 쿼리 호출 (limit, offset 전달)
        List<Board> boards = boardRepository.findAll(limit, offset);

        return boards.stream()
                .map(BoardResponse.ListDTO::new)
                .collect(Collectors.toList());
    }
}
