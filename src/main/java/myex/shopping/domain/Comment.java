package myex.shopping.domain;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
//@ToString(exclude = "post")
public class Comment {
    private Long id;
//    private Post post;
    private User user;
    private String content;
    private LocalDateTime createdDate = LocalDateTime.now();


}
