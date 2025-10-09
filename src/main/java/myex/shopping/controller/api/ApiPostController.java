package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.dbdto.PostDBDto;
import myex.shopping.form.PostForm;
import myex.shopping.repository.PostRepository;
import myex.shopping.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
@Validated
public class ApiPostController {
    private final PostRepository postRepository;
    private final PostService postService;


    //게시판 조회.
    @GetMapping
    public ResponseEntity<List<PostDBDto>> list() {
//        List<Post> posts = postRepository.findAll();
        List<PostDBDto> posts = postService.findAllPostDBDto();


        return ResponseEntity.ok(posts);
    }


    //게시물 등록
//<!--post(Form)   :         title, content, userId-->
//    Post(domain) : id(DB), title, content, userId, author, createdDate, comments
    @PostMapping("/new")
    public ResponseEntity<?> create(@Valid @RequestBody PostForm form,
                                    HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setCreatedDate(LocalDateTime.now());
        post.setAuthor(loginUser.getName());

        Post save = postService.addUser(post, loginUser.getId());

//        post.setUserId(loginUser.getId());

        System.out.println("save = " + save);
        return ResponseEntity.status(HttpStatus.CREATED).body(save);
    }

    //게시물 한개 보기.
    @GetMapping("/{id}")
    public ResponseEntity<Post> view(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long id) {

        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                //Post 있으면 post -> ResponseEntity.ok(post) 생성자로 넣어줌.
                .orElse(ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long id) {

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        postRepository.delete(id);
        return ResponseEntity.noContent().build(); //204 응답.
    }

}
