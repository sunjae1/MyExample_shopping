package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Order;
import myex.shopping.domain.User;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ApiOrderController {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;


    @PostMapping("/order")
    public ResponseEntity<?> order_change(HttpSession session) {

        //주문 실패 로직.
        //장바구니가 아무것도 없을때 주문하기 눌르면,
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null ||  cart.getCartItems().isEmpty()   ) {

            return ResponseEntity.badRequest()
                    .body("주문 불가 : 장바구니에 상품을 담아주세요.");
        }

        //주문 성공 로직.
        //Order 생성
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        Order order = new Order(loginUser);
        //OrderItem 생성 : 장바구니를 주문으로 전환.
        // Cart : CartItem ==> Order : OrderItem 전환.
        Order checkout = orderService.checkout(order, cart, loginUser);
        //repository에 저장.
        orderRepository.save(checkout);

        //재고 감소(주문 체결) + 장바구니 아이템 비우기.
        //Order.status : ORDERED --> PAID /현재는 동시에 바뀜.
        order.confirmOrder();
        session.removeAttribute("CART");

        return ResponseEntity.status(HttpStatus.CREATED).body(checkout);
        //생성 완료. /주문 완료된 order 보여줌.
    }


    @GetMapping("/orderAll")
    public ResponseEntity<List<Order>> orderAll() {
        List<Order> orderAll = orderRepository.findAll();
        return ResponseEntity.ok(orderAll);
    }

    //주문 취소. : items/{id}/cancel
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<?> orderCancel(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElse(null);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        order.cancel(); // 상태 변경 + 재고 복원

      return ResponseEntity.ok(order);
    }




}
