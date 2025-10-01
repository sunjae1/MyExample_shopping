package myex.shopping.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
//@ToString(exclude = "post")
@Entity
public class Comment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String content;
    private LocalDateTime createdDate = LocalDateTime.now();


}
