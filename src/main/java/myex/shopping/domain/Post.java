package myex.shopping.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Post {

    //게시판 : DB id, 제목, 내용, 글쓴이

    @Id @GeneratedValue
    private Long id;

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;
    @NotEmpty(message = "내용을 입력해주세요")
    private String content;

    private String author;

    //User로 바꾸고, @ManyToOne?
    private Long userId;
    private LocalDateTime createdDate;

    //cascade는 em 엔티티 기준 같이 쿼리 나감. orphanRemoval은 객체 기준, 객체 삭제시 자동으로 자식 객체 고아 삭제.
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //연관관계 편의 메소드
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void deleteComment(Comment comment)
    {
        comments.remove(comment);
        comment.setPost(null);
    }

}
