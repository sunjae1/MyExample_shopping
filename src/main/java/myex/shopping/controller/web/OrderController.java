package myex.shopping.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.*;
import myex.shopping.dto.orderdto.OrderDBDto;
import myex.shopping.repository.CartRepository;
import myex.shopping.service.CartService;
import myex.shopping.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class OrderController {

    private final OrderService orderService;
    private final CartRepository cartRepository;
    private final CartService cartService;


/*  장바구니로 감. 여기선, 장바구니 -> 주문으로 전환만.
    @GetMapping("/{itemId}/order")
    public String orderView(@PathVariable("itemId")Long itemId,
                            Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "order/orderForm";
    }*/
/*

    @PostMapping("/{itemId}/order")
    public String order(@PathVariable("itemId") Long itemId,
                        @ModelAttribute OrderForm orderForm,
                        Model model,
                        HttpServletRequest request) {
        //날아오는거. itemId, quantity(수량)
        Item findItem = itemRepository.findById(orderForm.getId()); //주소 자체 반환. itemRepository update 안해도 됨.

        //OrderItem 생성
        OrderItem orderItem = new OrderItem(findItem, findItem.getPrice(), orderForm.getQuantity());

        //Order 생성
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute("loginUser");

        Order order = new Order(loginUser);
        order.addOrderItem(orderItem);

        //repository에 저장.

        orderRepository.save(order);

        //재고 감소(주문 체결)
        order.confirmOrder();


        return "redirect:/main"; //주문 완료 페이지
    }
*/
    //장바구니 전체 -> 주문으로 변환.
    @PostMapping("/order")
    public String order_change(Model model,
                        HttpSession session) {
        //주문 실패 로직.
        //장바구니가 아무것도 없을때 주문하기 눌르면,
        User loginUser = (User) session.getAttribute("loginUser");
        Cart cart = cartService.findOrCreateCartForUser(loginUser);
        if (cart == null ||  cart.getCartItems().isEmpty()) {
            log.info("장바구니에 아무것도 없을 때");
            model.addAttribute("empty_cart_error", "주문 불가 : 장바구니에 상품을 담아주세요.");
            return "cart/cart_view";
        }
        //주문 성공 로직.
        //Order 생성.
        Order order = new Order(loginUser);
        Cart findCart = cartRepository.findByUser(loginUser)
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 없음"));
        //OrderItem 생성 : 장바구니를 주문으로 전환.
        // Cart : CartItem ==> Order : OrderItem 전환.
        log.info("장바구니 -> 주문 전환 전 : checkout 전");
        Order checkout = orderService.checkout(order, findCart, loginUser);
        log.info("장바구니 -> 주문 전환 후 : order 정보 {} ", checkout);
        cartService.deleteCart(cart.getId(),loginUser.getId());
//        session.removeAttribute("CART"); // 수정 예정.

/*        // User - Carts
        Optional<User> byId = userRepository.findById(loginUser.getId());
        User user = byId.get();

        //영속성으로 연관관계 편의 메소드 사용해야함.
        user.deleteCart(findCart);
*/
        /*
        //User 리스트 확인중.
        for (Cart userCart : user.getCarts()) {
            System.out.println("userCart = " + userCart);
        }
        */
        //CasCadeType.ALL 때문에 user.carts, List에 남은걸 제거를 안해서 Transactional 끝날때 같이 persist 해버림.
        //변화한걸 트랜잭션 끝날때 같이 전파 해버림. -> 연관관계 편의 메소드로 끊어야 됨. 없애야됨.
        return "redirect:/main"; //주문 완료 후 메인 페이지로 Redirect.
    }
    //주문 전체 조회.
    @GetMapping("/orderAll")
    public String orderAll(Model model,
                           HttpSession session) {
        List<OrderDBDto> orderAll = orderService.findAllOrderDtos();
        User loginUser = (User) session.getAttribute("loginUser");
        log.info("orderDB DTO List 정보 : {}", orderAll);
        model.addAttribute("orders", orderAll);
        model.addAttribute("loginUser", loginUser);
        return "order/order_view";
    }
    //주문 취소. : items/{id}/cancel
    @PostMapping("/{id}/cancel")
    public String orderCancel(@PathVariable Long id,
                              @RequestParam(required = false) String redirectInfo) {
        orderService.orderCancel(id);
        //마이 페이지에서 주문 취소할 시 redirect 분기.
        if ("mypage".equals(redirectInfo))
        {
            return "redirect:/mypage";
        }
        return "redirect:/items/orderAll";
    }
}
