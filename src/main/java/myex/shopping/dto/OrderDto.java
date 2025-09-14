package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderDto {
    private Long id;
    private List<OrderItemDto> orderItems;
    private LocalDateTime orderDate;
    private String status;

    public OrderDto(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus().name();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }
}
