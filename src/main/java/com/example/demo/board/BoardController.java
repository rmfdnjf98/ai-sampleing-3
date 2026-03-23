package com.example.demo.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo._core.handler.ex.Exception400;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

    private final BoardService boardService;
    private final HttpSession session;

    @GetMapping("/")
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        // 모든 유효성 검사와 데이터 준비는 Service가 수행한다.
        model.addAttribute("model", boardService.게시글목록보기(page));
        return "board/list";
    }

    @GetMapping("/board/save-form")
    public String saveForm() {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception400("로그인이 필요합니다.");
        }
        return "board/save-form";
    }

    @PostMapping("/board/save")
    public String save(BoardRequest.Save requestDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception400("로그인이 필요합니다.");
        }
        boardService.게시글쓰기(requestDTO, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        BoardResponse.DetailDTO detailDTO = boardService.게시글상세보기(id, sessionUser);
        model.addAttribute("model", detailDTO);
        return "board/detail";
    }

    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable int id) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception400("로그인이 필요합니다.");
        }
        boardService.게시글삭제(id, sessionUser);
        return "redirect:/";
    }

    @GetMapping("/board/{id}/update-form")
    public String updateForm(@PathVariable int id, Model model) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception400("로그인이 필요합니다.");
        }
        BoardResponse.DetailDTO detailDTO = boardService.게시글수정화면보기(id, sessionUser);
        model.addAttribute("model", detailDTO);
        return "board/update-form";
    }

    @PostMapping("/board/{id}/update")
    public String update(@PathVariable int id, BoardRequest.Save requestDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            throw new Exception400("로그인이 필요합니다.");
        }
        boardService.게시글수정(id, requestDTO, sessionUser);
        return "redirect:/board/" + id;
    }
}
