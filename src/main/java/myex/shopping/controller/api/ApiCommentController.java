package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.PostRepository;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Validated
public class ApiCommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //댓글 추가 : @RequestParam : form-data로 보내기.
    //@RequestBody : Dto 써서, JSON 으로 보내기 가능. (고민중)
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long postId,
                                        @RequestParam String reply_content,
                                        HttpSession session) {
        Post post = postRepository.findById(postId)
                .orElse(null);
        if (post ==null) {
            return ResponseEntity.notFound().build();
        }
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Comment comment = new Comment();
        comment.setUser(loginUser);
        comment.setContent(reply_content);
        post.addComment(comment);
        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED).body(comment);

    }

    //댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable @Positive(message = "양수만 입력가능합니다.") Long postId,
                                           @PathVariable @Positive(message = "양수만 입력가능합니다.") Long commentId,
                                           HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElse(null);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        //작성자 본인만 삭제 가능
        //본인이 아닌 유저 요청이 왔을 경우.
        if (!comment.getUser().getId().equals(loginUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("댓글 작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(commentId);
        post.deleteComment(comment);

        //삭제 성공/실패와 상관없이 다시 게시글 상세 페이지로
        return ResponseEntity.noContent().build(); //204 No Content
//        return ResponseEntity.ok(post);

    }

}
