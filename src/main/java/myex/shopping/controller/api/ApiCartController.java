package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.dto.RemoveCartDto;
import myex.shopping.form.CartForm;
import myex.shopping.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/items")
public class ApiCartController {
    private final ItemRepository itemRepository;


    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    // id, price, quantity --> CartForm : id(상품 id 넣기), quantity 매핑.

    //?????? 이상한데 itemId 하거나 CartForm id 쓰거나 한개만 써.
    @PostMapping("/{itemId}/cart")
    public ResponseEntity<Cart> addToCart(@PathVariable Long itemId,
                                          @RequestBody CartForm cartForm,
                                          HttpSession session) {


        Cart cart = getOrCreateCart(session);
//        Item findItem = itemRepository.findById(cartForm.getId());
        Item findItem = itemRepository.findById(itemId);

        if (findItem == null) {
            return ResponseEntity.notFound().build();
        }

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
    public ResponseEntity<Cart> cartItemRemove(@RequestBody RemoveCartDto cartDto,
                                               HttpSession session) {
        Item findItem = itemRepository.findById(cartDto.getItemId());
        
        if (findItem == null) {
            return ResponseEntity.notFound().build();
        }
        
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
