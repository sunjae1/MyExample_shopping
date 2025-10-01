package myex.shopping.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void deleteComment(Comment comment)
    {
        comments.remove(comment);
    }

}
