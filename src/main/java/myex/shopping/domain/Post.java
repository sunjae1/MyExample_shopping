package myex.shopping.domain;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Post {

    //게시판 : DB id, 제목, 내용, 글쓴이

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long userId;
    private LocalDateTime createdDate;

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
