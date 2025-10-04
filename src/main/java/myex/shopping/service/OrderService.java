package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    //장바구니 --> 주문 으로 전환.
    @Transactional
    public Order checkout(Order order, Cart cart, User user)
    {
        for (CartItem cartItem : cart.getCartItems()) {

            //DB에서 직접 item 조회해서 영속 상태 item 가져옴.(재고 반영을 위해서)
            Item persistentItem = itemRepository.findById(cartItem.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            order.addOrderItem(new OrderItem(persistentItem,cartItem.getItem().getPrice(), cartItem.getQuantity()));
        }

        orderRepository.save(order); //order-orderItem CASCADE.ALL 이여서 save시 같이 INSERT 문 날라가고, 같이 영속성 컨텍스트로 관리됨.
        
        //재고 감소 : 더티체킹 UPDATE 쿼리.
        order.confirmOrder();


        return order;
    }

    @Transactional
    public void orderCancel(Order order) {
        order.cancel(); // 재고 올리고 상태 CANCELLED 로 바뀜, 주문내역은 사라지지 않음.(기록용)

    }


}
