package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //댓글 추가
    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String reply_content,
                             HttpSession session) {
        Post post = postRepository.findById(postId)
                .orElseThrow();
        User loginUser = (User) session.getAttribute("loginUser");

        Comment comment = new Comment();
        comment.setUser(loginUser);
//        comment.setPost(post);
        comment.setContent(reply_content);
        post.addComment(comment);
        commentRepository.save(comment);

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

        Post post = postRepository.findById(postId).orElse(null);


        model.addAttribute("loginUser",loginUser);

        //없는 댓글이면 게시글 보기 페이지로 리다이렉트
        if (comment == null) {
            return "redirect:/posts/{postId}";
        }

        //작성자 본인만 삭제 가능
        if (loginUser != null && comment.getUser().getId().equals(loginUser.getId())) {
            commentRepository.delete(commentId);
            post.deleteComment(comment);

        }

        //삭제 성공/실패와 상관없이 다시 게시글 상세 페이지로
        return "redirect:/posts/{postId}";

    }

}
