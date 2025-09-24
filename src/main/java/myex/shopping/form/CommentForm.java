package myex.shopping.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentForm {
    @NotEmpty(message = "댓글 내용을 입력해주세요")
    private String content;

    // optional : postId도 넣을 수 있다.
    private Long postId;
}
