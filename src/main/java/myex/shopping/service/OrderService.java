package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;

    //장바구니 --> 주문 으로 전환.
//    @Transactional
    public Order checkout(Order order, Cart cart, User user)
    {
        for (CartItem cartItem : cart.getCartItems()) {

            //DB에서 직접 item 조회해서 영속 상태 item 가져옴.(재고 반영을 위해서)
            Item persistentItem = itemRepository.findById(cartItem.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            order.addOrderItem(new OrderItem(persistentItem,cartItem.getItem().getPrice(), cartItem.getQuantity()));
        }
        return order;
    }
}
