package myex.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import myex.shopping.domain.*;
import myex.shopping.dto.userdto.LoginRequestDto;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.CartService;
import myex.shopping.service.OrderService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    private User testUser;
    private Item testItem;
    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new User("testuser@example.com", "테스트유저", "password");
        userRepository.save(testUser);

        testItem = new Item("테스트 상품", 10000, 10, "path");
        itemRepository.save(testItem);

        session = getLoginSession(testUser.getEmail(), "password");
    }

    @Test
    @DisplayName("장바구니 상품으로 주문 생성 API 테스트")
    void createOrder_fromCart() throws Exception {
        // given
        Cart cart = cartService.findOrCreateCartForUser(testUser);
        cart.addItem(testItem, 2);
        
        // when & then
        mockMvc.perform(post("/api/items/order")
                        .session(session))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderItems", hasSize(1)))
                .andExpect(jsonPath("$.orderItems[0].itemName", is(testItem.getItemName())))
                .andExpect(jsonPath("$.status", is("PAID")));

        // 주문 후 장바구니가 비워졌는지 확인
        Cart updatedCart = cartService.findOrCreateCartForUser(testUser);
        assertThat(updatedCart.getCartItems()).isEmpty();
    }
    
    @Test
    @DisplayName("주문 취소 API 테스트")
    void cancelOrder_shouldChangeStatusToCancelled() throws Exception {
        // given
        Cart cart = cartService.findOrCreateCartForUser(testUser);
        cart.addItem(testItem, 2);
        Order order = orderService.checkout(testUser);
        
        // when & then
        mockMvc.perform(delete("/api/items/{id}/cancel", order.getId())
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

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
