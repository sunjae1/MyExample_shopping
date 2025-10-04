package myex.shopping.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.form.CommentForm;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //댓글 추가
    public Comment addComment(Long postId, CommentForm form, HttpSession session) {
        //Transactional 있어서 조회시 영속성이 관리.(Transactional 컨트롤러 -> 서비스 책임 이동.)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다."));

        User loginUser = (User) session.getAttribute("loginUser");

        Comment comment = new Comment();
        comment.setUser(loginUser);
        comment.setContent(form.getContent());
        post.addComment(comment); //연관관계 편의 메소드
        commentRepository.save(comment); //CASCADE.ALL 아직 없음. (고민중)

        return comment;
    }

    //댓글 삭제
    public void deleteComment(Long postId, Long commentId) {

        //영속성이 관리.
        //이미 컨트롤러에서 검증해서 2차 검증이라 값은 거의 존재.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물이 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        //orphanRemoval 안했을 때, 연관관계 주인 쪽에서 없애는 코드 있으면 그때 영속성이 관리한다면 UPDATE 쿼리 나감. --> Transactional 없으면 준영속으로 UPDATE 쿼리 안나감.
        post.deleteComment(comment); //외래키만 제거 : UPDATE 문. -> post_id를 null 처리만 함.
        commentRepository.delete(commentId); //comment 레코드 자체를 제거. -> post_id 외래 키도 함께 사라짐.

        /* update, delete 문 둘 다 나가지 않음. update 외래키 null 예약하고, delete comment_id 있으면 delete 만 보내서 하는 식으로 JPA 가 최적화 실행 함..*/
    }
}
