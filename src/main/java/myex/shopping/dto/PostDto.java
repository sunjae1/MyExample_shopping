package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostDto {
    private Long id;
    private String title;
    private String author;
    private String content;
    private LocalDateTime createdDate;
    private List<CommentDto> comments;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.author = post.getAuthor();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.comments = post.getComments().stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }
}
