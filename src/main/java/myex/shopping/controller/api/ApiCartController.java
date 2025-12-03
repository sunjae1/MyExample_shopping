package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.domain.User;
import myex.shopping.dto.cartdto.CartDto;
import myex.shopping.dto.cartdto.RemoveCartDto;
import myex.shopping.exception.ResourceNotFoundException;
import myex.shopping.form.CartForm;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import myex.shopping.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;
import jakarta.persistence.EntityManager;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
@Tag(name = "Cart", description = "장바구니 관련 API")
@Validated
public class ApiCartController {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final EntityManager em;


    @Operation(
            summary = "선택 상품 장바구니 저장",
            description = "한 상품에 대해서 선택 시 장바구니에 저장",
            responses = {
                    @ApiResponse(responseCode = "200", description = "장바구니 추가 성공"),
                    @ApiResponse(responseCode = "400", description = "클라이언트 오류(재고 수량 초과)"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패"),
                    @ApiResponse(responseCode = "404", description = "없는 상품 장바구니 추가")
            }
    )
    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    //입력값 : id, price, quantity --> 매핑값 : CartForm : id(itemId), quantity 매핑.
    // (2개 필드 매핑)
    //itemId나 CartForm id 중 하나만 쓰기. --> itemId로.
    @PostMapping("/{itemId}/cart")
    public ResponseEntity<CartDto> addToCart(@PathVariable @Positive(message = "양수만 입력가능합니다.") Long itemId,
                                          @Valid @RequestBody CartForm cartForm,
                                          HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            log.info("로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();//401
        }
        //많은 작업 -> 변수 추출 /Stream() -> 더 복잡.
        Optional<Item> findItemOpt = itemRepository.findById(itemId);
        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); //없는 상품을 추가 시 404
        }
        Item findItem = findItemOpt.get();
        //재고 수량 초과로 장바구니 담을 시
        if(findItem.getQuantity() < cartForm.getQuantity()) {
            log.info("재고 수량 초과로 장바구니 담을 시");
            return ResponseEntity.badRequest().build(); //클라이언트 오류 400
        }
        Cart cart = cartService.findOrCreateCartForUser(loginUser);
        //아이템과 수량.
        boolean result = cart.addItem(findItem, cartForm.getQuantity());
        //전체 장바구니 수량이 총 수량을 넘었을 때
        if (result == false) {
            log.info("전체 장바구니 수량이 총 수량을 넘었을 때");
            return ResponseEntity.badRequest().build(); //클라이언트 오류 400
        }
        // 장바구니 내용 변경 후 DB에 저장 (더티 체킹을 통해 변경 감지)
        cartRepository.save(cart); // 변경된 cart 엔티티를 명시적으로 저장

        // refresh를 통해 현재 트랜잭션의 cart 객체를 최신 DB 상태로 업데이트
        em.flush(); // 변경사항을 DB에 반영
        em.refresh(cart); // DB의 최신 상태로 cart 객체를 갱신

        CartDto cartDto = new CartDto(cart); // Use the refreshed cart
        return ResponseEntity.ok(cartDto); //200
    }

    @Operation(
            summary = "장바구니 전체 조회",
            description = "한 사용자의 장바구니 전체를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",description = "조회 성공"),
                    @ApiResponse(responseCode = "404",description = "해당 유저 장바구니를 못 찾음.")
            }
    )
    //장바구니 전체 보여주는 뷰. cartItem List 전달.
    @GetMapping("/cartAll")
    public ResponseEntity<CartDto> cartAll(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        return cartRepository.findByUser(loginUser)
                .map(cart -> ResponseEntity.ok(new CartDto(cart)))
                .orElse(null); // null 해야 빈 장바구니 출력 가능.
    }

    @Operation(
            summary = "장바구니 상품 하나 삭제",
            description = "전체 장바구니에서 상품 하나를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "삭제 상품을, 해당 사용자의 장바구니를 못찾았을때")
            }
    )
    //장바구니 아이템 삭제
    @DeleteMapping("/{itemId}/cart")
    public ResponseEntity<?> cartItemRemove(@PathVariable @Positive Long itemId,
                                               HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Item> findItemOpt = itemRepository.findById(itemId);
        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item findItem = findItemOpt.get();
        Cart cart = cartService.deleteItem(findItem.getId(), session);
        return ResponseEntity.ok(new CartDto(cart));
    }
    //메소드
    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("CART", cart);
        }
        return cart;
    }
}
