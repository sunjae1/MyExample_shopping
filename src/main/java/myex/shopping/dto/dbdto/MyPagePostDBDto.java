package myex.shopping.dto.dbdto;

import lombok.Getter;
import myex.shopping.domain.Post;
import myex.shopping.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MyPagePostDBDto {
    private Long id;
    private String title;
    private String content;
    private String authorName; //User 엔티티 대신 user.getName()

    private LocalDateTime createdDate;


    public MyPagePostDBDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.authorName = post.getUser().getName(); //LAZY 로딩 초기화

    }
}
