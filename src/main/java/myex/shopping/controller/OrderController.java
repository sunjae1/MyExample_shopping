package myex.shopping.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.domain.Order;
import myex.shopping.domain.OrderItem;
import myex.shopping.domain.User;
import myex.shopping.form.OrderForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class OrderController {

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;


    @GetMapping("/{itemId}/order")
    public String orderView(@PathVariable("itemId")Long itemId,
                            Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "order/orderForm";
    }

    @PostMapping("{itemId}/order")
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

    @GetMapping("/orderAll")
    public String orderAll(Model model) {
        List<Order> orderAll = orderRepository.findAll();
        model.addAttribute("orders", orderAll);
        return "order/order_view";
    }
}
