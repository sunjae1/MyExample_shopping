package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.postdto.PostDBDto;
import myex.shopping.form.PostForm;
import myex.shopping.repository.PostRepository;
import myex.shopping.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post", description = "게시물 관련 API")
@Validated
public class ApiPostController {
    private final PostRepository postRepository;
    private final PostService postService;
    @Operation(
            summary = "전체 게시물 조회",
            description = "전체 게시물을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    //게시판 조회.
    @GetMapping
    public ResponseEntity<List<PostDBDto>> list() {
        List<PostDBDto> posts = postService.findAllPostDBDto();
        return ResponseEntity.ok(posts);
    }
    @Operation(
            summary = "게시물 등록",
            description = "게시물을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "401", description = "로그인 실패"),
                    @ApiResponse(responseCode = "201", description = "등록 완료")
            }
    )
    //게시물 등록
//<!--post(Form)   :         title, content, userId-->
//    Post(domain) : id(DB), title, content, userId, author, createdDate, comments
    @PostMapping("/new")
    public ResponseEntity<?> create(@Valid @RequestBody PostForm form,
                                    HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.info("로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setCreatedDate(LocalDateTime.now());
        post.setAuthor(loginUser.getName());
        PostDBDto postDBDto = new PostDBDto(postService.addUser(post, loginUser.getId()));
        log.info("저장된 postDTO : {}",postDBDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postDBDto);
    }

    @Operation(
            summary = "게시물 한 개 보기",
            description = "게시물 한 개를 상세보기 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "조회 실패")
            }
    )
    //게시물 한개 보기.
    @GetMapping("/{id}")
    public ResponseEntity<PostDBDto> view(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long id) {

        return postRepository.findById(id)
                .map(PostDBDto::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }
    @Operation(
            summary = "게시물 삭제",
            description = "한 게시물을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "404", description = "게시물 찾지 못함"),
                    @ApiResponse(responseCode = "204", description = "삭제 성공")
            }
    )
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
