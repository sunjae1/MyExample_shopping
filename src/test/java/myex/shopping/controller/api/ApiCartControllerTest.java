package myex.shopping.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.domain.User;
import myex.shopping.dto.userdto.LoginRequestDto;
import myex.shopping.form.CartForm;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.CartService;
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
public class ApiCartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private EntityManager em;

    private User testUser;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser@example.com", "테스트유저", "password");
        userRepository.save(testUser);

        testItem = new Item("테스트 상품", 10000, 10, "path");
        itemRepository.save(testItem);
        
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("장바구니에 상품 추가 API 테스트: POST /api/items/{itemId}/cart")
    void addToCart_shouldAddItem() throws Exception {
        // given
        MockHttpSession session = getLoginSession(testUser.getEmail(), "password");
        CartForm cartForm = new CartForm();
        cartForm.setId(testItem.getId());
        cartForm.setQuantity(2); // 2개 담기

        // when & then
        mockMvc.perform(post("/api/items/{itemId}/cart", testItem.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartForm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].item.itemName", is(testItem.getItemName())))
                .andExpect(jsonPath("$.cartItems[0].quantity", is(2)))
                .andExpect(jsonPath("$.allPrice", is(20000)));
    }
    
    @Test
    @DisplayName("장바구니 조회 API 테스트: GET /api/items/cartAll")
    void getCart_shouldReturnCartContents() throws Exception {
        // given
        MockHttpSession session = getLoginSession(testUser.getEmail(), "password");
        // 상품을 장바구니에 미리 추가
        Cart cart = cartService.findOrCreateCartForUser(testUser);
        cart.addItem(testItem, 3);
        cartRepository.save(cart); // cart가 새로 생성된 경우 DB에 반영
        em.flush();

        // when & then
        mockMvc.perform(get("/api/items/cartAll")
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(1)))
                .andExpect(jsonPath("$.cartItems[0].item.itemName", is(testItem.getItemName())))
                .andExpect(jsonPath("$.allPrice", is(30000)));
    }

    @Test
    @DisplayName("장바구니 상품 삭제 API 테스트: DELETE /api/items/{itemId}/cart")
    void removeCartItem_shouldRemoveItem() throws Exception {
        // given
        MockHttpSession session = getLoginSession(testUser.getEmail(), "password");
        Cart cart = cartService.findOrCreateCartForUser(testUser);
        cart.addItem(testItem, 5);
        cartRepository.save(cart); // cart가 새로 생성된 경우 DB에 반영
        em.flush();
        em.clear();
        
        // when
        mockMvc.perform(delete("/api/items/{itemId}/cart", testItem.getId())
                        .session(session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems", hasSize(0)))
                .andExpect(jsonPath("$.allPrice", is(0)));

        // then
        // 직접 DB를 조회하여 확인
        Cart updatedCart = cartRepository.findByUser(testUser).orElseThrow();
        assertThat(updatedCart.getCartItems()).isEmpty();
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
