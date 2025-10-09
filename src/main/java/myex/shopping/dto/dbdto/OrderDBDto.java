package myex.shopping.dto.dbdto;

import lombok.Getter;
import myex.shopping.domain.Order;
import myex.shopping.domain.OrderStatus;
import myex.shopping.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderDBDto {
    private Long id;
//    user 나중에 고민.(마이 페이지라 세션에서 꺼내면 될꺼 같기도)
    private User user;
    private Integer totalPrice;//메소드 미리 받아둠.
    private Integer totalQuantity;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private List<MyPageOrderItemDto> orderItems; //OrderItem을 표현할 DTO

    private boolean orderIsNotCanCelled;

    public OrderDBDto(Order order) {
        this.id = order.getId();

        this.user = order.getUser();

        this.totalPrice = order.getTotalPrice();
        this.totalQuantity = order.getTotalQuantity();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
        this.orderItems = order.getOrderItems().stream() //LAZY 초기화
                .map(MyPageOrderItemDto::new)
                .collect(Collectors.toList());

        this.orderIsNotCanCelled = order.orderIsNotCanCelled();
    }

    @Override
    public String toString() {
        return "OrderDBDto{" +
                "id=" + id +
                ", user=" + user +
                ", totalPrice=" + totalPrice +
                ", totalQuantity=" + totalQuantity +
                ", status=" + status +
                ", orderDate=" + orderDate +
                '}';
    }
}
