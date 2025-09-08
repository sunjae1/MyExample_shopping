package myex.shopping;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.domain.Order;
import myex.shopping.domain.OrderItem;
import myex.shopping.domain.User;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component //클래스 레벨에서 스프링 빈으로 등록
@RequiredArgsConstructor
public class TestDataInit {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct //스프링 빈이 생성되고, 의존성 주입이 끝난 뒤 호출되는 메서드 (빈 이 다 준비되면 자동으로 메소드 실행
    public void init() {
        Item itemA = new Item("아이템A", 2000, 10, "/img/1.webp");
        itemRepository.save(itemA);
        itemRepository.save(new Item("아이템B", 4000,20,"/img/2.webp"));

        User user = new User("test@na.com","테스터","test!");
        userRepository.save(user);

        Integer price = itemA.getPrice();
        int quantity = 3;

        //OrderItem, Order 테스트 데이터 생성.


        OrderItem orderItem = new OrderItem(itemA, price, quantity);
        Order order = new Order(user);
        order.addOrderItem(orderItem);
        orderRepository.save(order);
        order.confirmOrder();

    }



}
