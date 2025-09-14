package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentDto {
    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdDate;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getName();
        this.createdDate = comment.getCreatedDate();
    }
}
