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
    //장바구니 전체 -> 주문으로 변환.
    @PostMapping("/order")
    public String order_change(Model model,
                        HttpSession session) {
        //주문 실패 로직.
        //장바구니가 아무것도 없을때 주문하기 눌르면,
        User loginUser = (User) session.getAttribute("loginUser");
        Cart cart = cartService.findOrCreateCartForUser(loginUser);
        if (cart == null ||  cart.getCartItems().isEmpty()) {
            log.info("주문 실패 로직 : 장바구니에 아무것도 없을 때");
            model.addAttribute("empty_cart_error", "주문 불가 : 장바구니에 상품을 담아주세요.");
            return "cart/cart_view";
        }
        //주문 성공 로직.
        //Order 생성.
        //OrderItem 생성 : 장바구니를 주문으로 전환.
        // Cart : CartItem ==> Order : OrderItem 전환.
        log.info("장바구니 -> 주문 전환 전 : checkout 전");
        Order checkout = orderService.checkout(loginUser);
        log.info("장바구니 -> 주문 전환 후 : order 정보 {} ", checkout);
        //장바구니 전체 삭제.
        cartService.deleteCart(cart.getId(),loginUser.getId());
        return "redirect:/main"; //주문 완료 후 메인 페이지로 Redirect.
    }
    //주문 전체 조회.
    @GetMapping("/orderAll")
    public String orderAll(Model model,
                           HttpSession session) {
        List<OrderDBDto> orderAll = orderService.findAllOrderDtos();
        User loginUser = (User) session.getAttribute("loginUser");
        log.info("orderDTO List 정보 : {}", orderAll);
        model.addAttribute("orders", orderAll);
        model.addAttribute("loginUser", loginUser);
        return "order/order_view";
    }
    //주문 취소. : items/{id}/cancel
    @PostMapping("/{id}/cancel")
    public String orderCancel(@PathVariable Long id,
                              HttpSession session,
                              @RequestParam(required = false) String redirectInfo) {
        User loginUser = (User) session.getAttribute("loginUser");
        orderService.orderCancel(id, loginUser);
        //마이 페이지에서 주문 취소할 시 redirect 분기.
        if ("mypage".equals(redirectInfo))
        {
            return "redirect:/mypage";
        }
        return "redirect:/items/orderAll";
    }
}
