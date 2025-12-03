package myex.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.userdto.LoginRequestDto;
import myex.shopping.repository.CommentRepository;
import myex.shopping.repository.PostRepository;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;

    private User testUser;
    private Post testPost;
    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("testuser@example.com", "테스트유저", "password");
        userRepository.save(testUser);

        testPost = new Post();
        testPost.setTitle("테스트 게시글");
        testPost.setContent("테스트 내용입니다.");
        testPost.setUser(testUser);
        postRepository.save(testPost);

        session = getLoginSession(testUser.getEmail(), "password");
    }

    @Test
    @DisplayName("댓글 추가 API 테스트")
    void addComment_shouldCreateComment() throws Exception {
        // when & then
        mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                        .session(session)
                        .param("reply_content", "새로운 댓글입니다."))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is("새로운 댓글입니다.")))
                .andExpect(jsonPath("$.username", is(testUser.getName())));
    }
    
    @Test
    @DisplayName("댓글 수정 API 테스트 - 성공")
    void updateComment_shouldUpdate() throws Exception {
        // given
        // 먼저 댓글을 하나 추가
/*        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                        .session(session)
                        .param("reply_content", "원본 댓글"))
                .andExpect(status().isCreated())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("jsonResponse = " + jsonResponse);
        Integer commentId = objectMapper.readTree(jsonResponse).get("id").asInt();
        System.out.println("commentId = " + commentId);*/

        Comment comment = new Comment();
        comment.setContent("원본 댓글");
        comment.setUser(testUser);
        testPost.addComment(comment);
        commentRepository.save(comment);


        // when & then
        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", testPost.getId(), comment.getId())
                        .session(session)
                        .param("reply_content", "수정된 댓글입니다."))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", is("수정된 댓글입니다.")));
    }
    
    @Test
    @DisplayName("댓글 삭제 API 테스트 - 성공")
    void deleteComment_shouldDelete() throws Exception {
        // given
        // 먼저 댓글을 하나 추가
/*        MvcResult result = mockMvc.perform(post("/api/posts/{postId}/comments", testPost.getId())
                        .session(session)
                        .param("reply_content", "삭제될 댓글"))
                .andExpect(status().isCreated())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        Integer commentId = objectMapper.readTree(jsonResponse).get("id").asInt();*/
        Comment comment = new Comment();
        comment.setContent("삭제될 댓글");
        comment.setUser(testUser);
        testPost.addComment(comment);
        commentRepository.save(comment);

        // when & then
        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", testPost.getId(), comment.getId())
                        .session(session))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    //다른 사용자 권한 불가 테스트 추가.


    private MockHttpSession getLoginSession(String email, String password) throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession();
    }
}
