package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/posts")
public class ApiPostController {
    private final PostRepository postRepository;


    //게시판 조회.
    @GetMapping
    public ResponseEntity<List<Post>> list() {
        List<Post> posts = postRepository.findAll();
        return ResponseEntity.ok(posts);
    }


    //게시물 등록
//<!--post(Form)   :         title, content, userId-->
//    Post(domain) : id(DB), title, content, userId, author, createdDate, comments
    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody Post post,
                                    HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        post.setUserId(loginUser.getId());
        post.setAuthor(loginUser.getName());

        Post save = postRepository.save(post);
        System.out.println("save = " + save);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    //게시물 한개 보기.
    @GetMapping("/{id}")
    public ResponseEntity<Post> view(@PathVariable Long id) {

        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                //Post 있으면 post -> ResponseEntity.ok(post) 생성자로 넣어줌.
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        postRepository.delete(id);
        return ResponseEntity.noContent().build(); //204 응답.
    }

}
