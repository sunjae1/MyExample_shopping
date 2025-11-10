package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.dbdto.PostDBDto;
import myex.shopping.form.CommentForm;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.memory.MemoryCommentRepository;
import myex.shopping.repository.PostRepository;
import myex.shopping.service.CommentService;
import myex.shopping.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final PostService postService;

    //댓글 추가
    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @Valid @ModelAttribute CommentForm form,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {



        Post post = postRepository.findById(postId)
                .orElseThrow(); //컨트롤러로 반환시 준영속 상태로 바뀜.(Transactional 없을 시)
        User loginUser = (User)session.getAttribute("loginUser");

        /*
        ModelAttribute는 PathVariable도 객체 멤버변수에 있으면 바인딩 해줌.
         */
        System.out.println("loginUser = " + loginUser);
        System.out.println("form = " + form);

        if (bindingResult.hasErrors()) {
            PostDBDto postDBDto = postService.changeToDto(postId);
            model.addAttribute("post", postDBDto);
            model.addAttribute("loginUser",loginUser);
            return "posts/view";
        }

        commentService.addComment(postId, form, session);

//        Comment comment = new Comment();
//        comment.setUser(loginUser);
//        comment.setContent(form.getContent());
//        post.addComment(comment);
//        commentRepository.save(comment);

        //PathVariable 은 url에서 변수로 받을 수 있고, 자동으로 redirect에서 받을 수 있다.

        return "redirect:/posts/"+postId;

    }

    //댓글 삭제
    @PostMapping("/{postId}/comments/{commentId}")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                HttpSession session,
                                Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        Comment comment = commentRepository.findById(commentId)
                .orElse(null);

        Post post = postRepository.findById(postId)
                .orElse(null);


        model.addAttribute("loginUser",loginUser);

        //없는 댓글이면 게시글 보기 페이지로 리다이렉트(잘못된 url 요청)
        if (comment == null) {
            return "redirect:/posts/{postId}";
        }

        //작성자 본인만 삭제 가능
        if (loginUser != null && comment.getUser().getId().equals(loginUser.getId())) {
            commentService.deleteComment(postId, commentId);
        }

        //삭제 성공/실패와 상관없이 다시 게시글 상세 페이지로
        return "redirect:/posts/{postId}";
    }

}
