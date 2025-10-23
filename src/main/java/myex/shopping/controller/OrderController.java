package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.dto.dbdto.MyPageOrderDto;
import myex.shopping.dto.dbdto.OrderDBDto;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.repository.memory.MemoryOrderRepository;
import myex.shopping.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class OrderController {

//    private final MemoryItemRepository memoryItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CartRepository cartRepository;


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
    @PostMapping("/order")
    public String order_change(Model model,
                        HttpSession session) {

        //주문 실패 로직.
        //장바구니가 아무것도 없을때 주문하기 눌르면,
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null ||  cart.getCartItems().isEmpty()   ) {
            model.addAttribute("empty_cart_error", "주문 불가 : 장바구니에 상품을 담아주세요.");
            return "cart/cart_view";
        }

        //주문 성공 로직.
        //Order 생성
        User loginUser = (User) session.getAttribute("loginUser");
        Order order = new Order(loginUser);

        Cart findCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니 없음"));

        //OrderItem 생성 : 장바구니를 주문으로 전환.
        // Cart : CartItem ==> Order : OrderItem 전환.
        Order checkout = orderService.checkout(order, findCart, loginUser);

        System.out.println("checkout = " + checkout);

        //repository에 저장.
//        orderRepository.save(checkout);

        //재고 감소(주문 체결) + 장바구니 아이템 비우기.
        //Order.status : ORDERED --> PAID /현재는 동시에 바뀜.
//        order.confirmOrder();
        cartRepository.delete(cart.getId());
        session.removeAttribute("CART");

        return "redirect:/main"; //주문 완료 페이지
    }


    @GetMapping("/orderAll")
    public String orderAll(Model model,
                           HttpSession session) {
//        List<Order> orderAll = orderRepository.findAll();
        List<OrderDBDto> orderAll = orderService.findAllOrderDtos();

        User loginUser = (User) session.getAttribute("loginUser");
        System.out.println("orderAll = " + orderAll);
        model.addAttribute("orders", orderAll);
        model.addAttribute("loginUser", loginUser);
        return "order/order_view";
    }

    //주문 취소. : items/{id}/cancel
    @PostMapping("/{id}/cancel")
    public String orderCancel(@PathVariable Long id,
                              @RequestParam(required = false) String redirectInfo) {
//        Order order = orderRepository.findById(id)
//                .orElse(null);
        //
        orderService.orderCancel(id);


        if ("mypage".equals(redirectInfo))
        {
            return "redirect:/mypage";
        }

        return "redirect:/items/orderAll";
    }

/*
//    @PostMapping("/{id}/cancelByUser")
    public String orderCancelByUser(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElse(null);
        order.cancel(); //orderItem 마다 재고 다 올리고 상태는 CANCELLED로 바뀜. 주문내역은 사라지지 않음.
        return "redirect:/mypage";
    }
*/



}
