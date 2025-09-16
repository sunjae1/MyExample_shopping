package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    //장바구니 --> 주문 으로 전환.
    public Order checkout(Order order, Cart cart, User user)
    {
        for (CartItem cartItem : cart.getCartItems()) {
            order.addOrderItem(new OrderItem(cartItem.getItem(),cartItem.getItem().getPrice(), cartItem.getQuantity()));
        }
        return order;
    }
}
