package myex.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import myex.shopping.domain.User;
import myex.shopping.dto.userdto.LoginRequestDto;
import myex.shopping.dto.userdto.UserEditDto;
import myex.shopping.form.RegisterForm;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.UserService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "테스트유저", "password123");
        userService.save(testUser);
    }

    @Test
    @DisplayName("회원가입 API 테스트: POST /api/register")
    void registerUser_shouldCreateUser() throws Exception {
        RegisterForm registerForm = new RegisterForm();
        registerForm.setEmail("newuser@example.com");
        registerForm.setName("새로운유저");
        registerForm.setPassword("newpassword");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerForm))
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$.email", is("newuser@example.com")))
                .andExpect(jsonPath("$.name", is("새로운유저")));

        User foundUser = userRepository.findByEmail("newuser@example.com").orElseThrow();
        assertThat(foundUser).isNotNull();
    }

    @Test
    @DisplayName("로그인 API 성공 테스트: POST /api/login")
    void login_shouldSucceed_withValidCredentials() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail(testUser.getEmail());
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(testUser.getEmail())))
                .andExpect(request().sessionAttribute("loginUser", is(testUser)));
    }

    @Test
    @DisplayName("로그인 API 실패 테스트: POST /api/login")
    void login_shouldFail_withInvalidCredentials() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setEmail(testUser.getEmail());
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("전체 회원 조회 API 테스트: GET /api/allUser")
    void getAllUsers_shouldReturnUserList() throws Exception {
        // 추가 유저 생성
        userService.save(new User("user2@example.com", "유저2", "pass"));

        mockMvc.perform(get("/api/allUser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("회원 정보 수정 API 테스트: PUT /api/update")
    void updateUser_shouldUpdateUserInfo() throws Exception {
        // given: 로그인된 세션 획득
        MockHttpSession session = getLoginSession(testUser.getEmail(), "password123");

        UserEditDto userEditDto = new UserEditDto();
        userEditDto.setName("수정된이름");
        userEditDto.setEmail("updated@example.com");

        // when & then
        mockMvc.perform(put("/api/update")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEditDto))
                        .characterEncoding("UTF-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().encoding("UTF-8"))
                .andExpect(jsonPath("$.name", is("수정된이름")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));

        // 세션 정보도 갱신되었는지 확인
        User updatedUserInSession = (User) session.getAttribute("loginUser");
        assertThat(updatedUserInSession.getName()).isEqualTo("수정된이름");
        assertThat(updatedUserInSession.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("회원 탈퇴 API 테스트: DELETE /api/delete")
    void deleteUser_shouldDeactivateUser() throws Exception {
        // given: 로그인된 세션 획득
        MockHttpSession session = getLoginSession(testUser.getEmail(), "password123");
        Long userId = testUser.getId();

        // when & then
        mockMvc.perform(delete("/api/delete")
                        .session(session))
                .andDo(print())
                .andExpect(status().isNoContent());

        // 유저가 비활성화 되었는지 확인 (DB에서 soft delete 가정)
        User deletedUser = userRepository.findById(userId).orElseThrow();
        assertThat(deletedUser.isActive()).isFalse();

        // 세션이 만료되었는지 확인
        assertThat(session.isInvalid()).isTrue();
    }
    
    /**
     * 테스트용 로그인 세션을 생성하여 반환하는 헬퍼 메서드
     */
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
