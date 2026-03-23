package com.example.demo.board;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo._core.handler.ex.Exception400;
import com.example.demo.user.User;

import lombok.RequiredArgsConstructor;

/**
 * DTO는 Service에서 만든다. Entity를 Controller에 전달하지 않는다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardResponse.ListDTO 게시글목록보기(int page) {
        // 1. 유효성 검사 (Service에서 수행)
        if (page < 1) {
            throw new Exception400("페이지 번호는 1보다 작을 수 없습니다.");
        }

        int limit = 3; // 한 페이지당 보여줄 개수
        int offset = (page - 1) * limit;

        List<Board> boards = boardRepository.findAll(limit, offset);
        Long totalCount = boardRepository.countAll();
        int totalPages = (int) Math.ceil((double) totalCount / limit);

        // 2. 상한선 유효성 검사
        if (page > totalPages && totalPages > 0) {
            throw new Exception400("존재하지 않는 페이지입니다.");
        }

        List<BoardResponse.BoardDTO> boardDTOs = boards.stream()
                .map(BoardResponse.BoardDTO::new)
                .collect(Collectors.toList());

        return new BoardResponse.ListDTO(boardDTOs, page - 1, totalPages);
    }

    public BoardResponse.DetailDTO 게시글상세보기(int id, User sessionUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new Exception400("해당 게시글을 찾을 수 없습니다."));

        return new BoardResponse.DetailDTO(board, sessionUser);
    }

    @Transactional
    public void 게시글쓰기(BoardRequest.Save requestDTO, User sessionUser) {
        Board board = Board.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .user(sessionUser)
                .build();

        boardRepository.save(board);
    }

    @Transactional
    public void 게시글삭제(int id, User sessionUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new Exception400("해당 게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(sessionUser.getId())) {
            throw new Exception400("게시글 삭제 권한이 없습니다.");
        }

        boardRepository.deleteById(id);
    }

    @Transactional
    public void 게시글수정(int id, BoardRequest.Save requestDTO, User sessionUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new Exception400("해당 게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(sessionUser.getId())) {
            throw new Exception400("게시글 수정 권한이 없습니다.");
        }

        board.setTitle(requestDTO.getTitle());
        board.setContent(requestDTO.getContent());
    }

    public BoardResponse.DetailDTO 게시글수정화면보기(int id, User sessionUser) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new Exception400("해당 게시글을 찾을 수 없습니다."));

        if (!board.getUser().getId().equals(sessionUser.getId())) {
            throw new Exception400("게시글 수정 권한이 없습니다.");
        }

        return new BoardResponse.DetailDTO(board, sessionUser);
    }
}
