package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.dto.RemoveCartDto;
import myex.shopping.form.CartForm;
import myex.shopping.repository.memory.MemoryItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
@Validated
public class ApiCartController {
    private final MemoryItemRepository memoryItemRepository;


    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    // id, price, quantity --> CartForm : id(상품 id 넣기), quantity 매핑.

    //?????? 이상한데 itemId 하거나 CartForm id 쓰거나 한개만 써.  --> itemId로.
    @PostMapping("/{itemId}/cart")
    public ResponseEntity<Cart> addToCart(@PathVariable @Positive(message = "양수만 입력가능합니다.") Long itemId,
                                          @Valid @RequestBody CartForm cartForm,
                                          HttpSession session) {


        Cart cart = getOrCreateCart(session);
//        Item findItem = itemRepository.findById(cartForm.getId());
        Optional<Item> findItemOpt = memoryItemRepository.findById(itemId);

        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item findItem = findItemOpt.get();
        //아이템과 수량.
        cart.addItem(findItem, cartForm.getQuantity());
        return ResponseEntity.ok(cart);

    }

    //장바구니 전체 보여주는 뷰.
    @GetMapping("/cartAll")
    public ResponseEntity<Cart> cartAll(HttpSession session) {
        Cart cart = getOrCreateCart(session);
        return ResponseEntity.ok(cart);

    }

    //장바구니 아이템 삭제
    //@RequestBody Long id 하면 JSON 에 { "itemId" : "1"} 이거는 객체형 그냥 1 이렇게 보내야함. JSON 으로 보낼 수 있는것들 검색.
    @DeleteMapping("/cart/remove")
    public ResponseEntity<Cart> cartItemRemove(@Valid @RequestBody RemoveCartDto cartDto,
                                               HttpSession session) {
        Optional<Item> findItemOpt = memoryItemRepository.findById(cartDto.getItemId());
        
        if (findItemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Item findItem = findItemOpt.get();

        Cart cart = getOrCreateCart(session);
        cart.removeItem(findItem);
        return ResponseEntity.ok(cart); //삭제 후 전체 장바구니 상태 반환
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
