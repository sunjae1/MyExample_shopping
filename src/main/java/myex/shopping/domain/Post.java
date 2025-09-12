package myex.shopping.domain;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {

    //게시판 : DB id, 제목, 내용, 글쓴이

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long userId;

    private LocalDateTime createdDate;

    public Post() {
    }

    public Post(Long id, String title, String content, String author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
    }


}
