package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.*;
import myex.shopping.dto.mypagedto.MyPageOrderDto;
import myex.shopping.dto.orderdto.OrderDBDto;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    //장바구니 --> 주문 으로 전환.
    public Order checkout(Order order, Cart cart, User user)
    {
        for (CartItem cartItem : cart.getCartItems()) {

            //DB에서 직접 item 조회해서 영속 상태 item 가져옴.(재고 반영을 위해서)
            Item persistentItem = itemRepository.findById(cartItem.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            order.addOrderItem(new OrderItem(persistentItem,cartItem.getItem().getPrice(), cartItem.getQuantity()));
        }


        //재고 감소 : 더티체킹 UPDATE 쿼리.
        order.confirmOrder();
        log.info("order.confirmOrder() 재고 감소 후");
        //order-orderItem CASCADE.ALL 이여서 save시 같이 INSERT 문 날라가고, 같이 영속성 컨텍스트로 관리됨.
        orderRepository.save(order); //GenerationType.IDENTITY 이므로, save() 호출 즉시 INSERT 쿼리 나감.
        log.info("checkout 메소드 save(order) 후");
        return order;
    }

    public void orderCancel(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문은 없습니다."));

        order.cancel(); // 재고 올리고 상태 CANCELLED 로 바뀜, 주문내역은 사라지지 않음.(기록용)

    }

    public List<MyPageOrderDto> changeToOrderDtoList(User user) {
        return orderRepository.findByUser(user)
                .stream()
                .map(MyPageOrderDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDBDto> findAllOrderDtos() {
        return orderRepository.findAll()
                .stream()
                .map(OrderDBDto::new)
                .collect(Collectors.toList());
    }


}
