package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.domain.User;
import myex.shopping.dto.CartDto;
import myex.shopping.dto.RemoveCartDto;
import myex.shopping.form.CartForm;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
@Validated
public class ApiCartController {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;



    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    // id, price, quantity --> CartForm : id(상품 id 넣기), quantity 매핑.

    //itemId나 CartForm id 중 하나만 쓰기. --> itemId로.
    @PostMapping("/{itemId}/cart")
    public ResponseEntity<CartDto> addToCart(@PathVariable @Positive(message = "양수만 입력가능합니다.") Long itemId,
                                          @Valid @RequestBody CartForm cartForm,
                                          HttpSession session) {


//        Cart cart = getOrCreateCart(session);
//        Item findItem = itemRepository.findById(cartForm.getId());
        Optional<Item> findItemOpt = itemRepository.findById(itemId);
        
        
        
        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item findItem = findItemOpt.get();
        
        //재고 수량 초과로 장바구니 담을 시
        if(findItem.getQuantity() < cartForm.getQuantity()) {
            return ResponseEntity.badRequest().build(); //클라이언트 오류
        }

        User loginUser = (User) session.getAttribute("loginUser");
        Cart cart = cartService.findOrCreateCartForUser(loginUser);

        //아이템과 수량.
        boolean result = cart.addItem(findItem, cartForm.getQuantity());

        //전체 장바구니 수량이 총 수량을 넘었을 때
        if (result == false) {
            return ResponseEntity.badRequest().build();
        }

        CartDto cartDto = new CartDto(cart);
        return ResponseEntity.ok(cartDto);

    }

    //장바구니 전체 보여주는 뷰. cartItem List를 보내줌.
    @GetMapping("/cartAll")
    public ResponseEntity<CartDto> cartAll(HttpSession session) {
//        Cart cart = getOrCreateCart(session);
        User loginUser = (User) session.getAttribute("loginUser");

        return cartRepository.findByUser(loginUser)
                .map(cart -> ResponseEntity.ok(new CartDto(cart)))
                .orElseGet(() -> ResponseEntity.notFound().build());

//        CartDto cartDto = new CartDto(cart);
//        return ResponseEntity.ok(cartDto);

    }

    //장바구니 아이템 삭제
    //@RequestBody Long id 하면 JSON 에 { "itemId" : "1"} 이거는 객체형 그냥 1 이렇게 보내야함. JSON 으로 보낼 수 있는것들 검색.
    @DeleteMapping("/cart/remove")
    public ResponseEntity<CartDto> cartItemRemove(@Valid @RequestBody RemoveCartDto removeCartDto,
                                               HttpSession session) {
        Optional<Item> findItemOpt = itemRepository.findById(removeCartDto.getItemId());
        
        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item findItem = findItemOpt.get();

//        Cart cart = getOrCreateCart(session);

        cartService.deleteItem(findItem.getId(), session);

//        cart.removeItem(findItem);
        User loginUser = (User) session.getAttribute("loginUser");

        return cartRepository.findByUser(loginUser)
                .map(cart -> ResponseEntity.ok(new CartDto(cart)))
                .orElseGet(() -> ResponseEntity.notFound().build());

//        CartDto cartDto = new CartDto(cart);
//        return ResponseEntity.ok(cartDto); //삭제 후 전체 장바구니 상태 반환
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
