package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.CommentDto;
import myex.shopping.form.CommentForm;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.memory.MemoryCommentRepository;
import myex.shopping.repository.PostRepository;
import myex.shopping.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 관련 API")
@Validated
public class ApiCommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;



    @Operation(
            summary = "댓글 추가",
            description = "로그인 사용자로 댓글을 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "게시물을 찾지 못함"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패"),
                    @ApiResponse(responseCode = "201", description = "댓글 생성 성공")
            }
    )
    //댓글 추가 : @RequestParam : form-data로 보내기.
    //@RequestBody : Dto 써서, JSON 으로 보내기 가능. (고민중)
//    @Transactional
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long postId,
                                        @RequestParam @NotBlank(message = "댓글 내용을 입력해주세요") String reply_content,
                                        HttpSession session) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Post post =  postOpt.get();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        CommentForm form = new CommentForm();
        form.setContent(reply_content);
        CommentDto commentDto = new CommentDto(commentService.addComment(postId, form, session));
/*
        Comment comment = new Comment();
        comment.setUser(loginUser);
        comment.setContent(reply_content);
        post.addComment(comment);
        commentRepository.save(comment);
*/
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);

    }

    @Operation(
            summary = "댓글 삭제",
            description = "로그인 사용자에 대해 댓글 삭제 요청을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "401", description = "로그인 실패"),
                    @ApiResponse(responseCode = "404", description = "댓글이나 게시물을 찾지 못함."),
                    @ApiResponse(responseCode = "403", description = "삭제 권한 없음(댓글 사용자 본인이 아님)"),
                    @ApiResponse(responseCode = "204", description = "삭제 완료")
            }
    )
    //댓글 삭제
//    @Transactional
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

        commentService.deleteComment(postId, commentId);

        //삭제 성공/실패와 상관없이 다시 게시글 상세 페이지로
        return ResponseEntity.noContent().build(); //204 No Content
    }

}
