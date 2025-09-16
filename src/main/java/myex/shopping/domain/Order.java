package myex.shopping.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
//프로퍼티 접근법 Thymeleaf 에서 쓰임.
public class Order {
    private Long id;
    private User user;
    private List<OrderItem> orderItems = new ArrayList<>();
    private LocalDateTime orderDate;
    private OrderStatus status;

    //주문 시.
    public Order(User user) {
        this.user = user;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.ORDERED;
    }

    //주문 아이템 추가
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    //총 금액 계산
    public int getTotalPrice() {
        //OrderItem 스트림을 int로 바꾸는 (OrderItem) -> int 인 function을 요구.
        return orderItems.stream()
                .mapToInt(OrderItem::getTotalPrice)
                .sum();
    }

    //총 수량 계산
    public int getTotalQuantity() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }



    //주문 확정 시 재고 감소 : 주문 --> 결제 --> 확정(orderItem이 item을 가져야만 decreaseStock() 사용가능.) 앞 사람이 재고를 다 털어가면 Exception 내야함.
    public void confirmOrder() {
        for (OrderItem orderItem : orderItems) {
            orderItem.getItem().decreaseStock(orderItem.getQuantity());

        }
        this.status =OrderStatus.PAID; //주문 체결.
    }

    //주문 취소
    public void cancel() {
        for (OrderItem orderItem : orderItems) {
            orderItem.getItem().increaseStock(orderItem.getQuantity());
        }
        this.status =OrderStatus.CANCELLED;
    }


    //전체 장바구니 --> 주문 버튼으로.
    //장바구니 -> Order 로 전환
    public Order checkout(Order order, Cart cart, User user) {
        for (CartItem ci : cart.getCartItems()) {
            order.addOrderItem(new OrderItem(ci.getItem(), ci.getItem().getPrice(), ci.getQuantity()));
        }
        return order;
    }

    public boolean orderIsNotCanCelled() {

        if (this.status ==OrderStatus.CANCELLED)
        {
            return false;
        }
        else {
            return true;
        }
    }



}
