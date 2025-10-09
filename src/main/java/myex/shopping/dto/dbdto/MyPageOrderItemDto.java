package myex.shopping.dto.dbdto;

import lombok.Getter;
import myex.shopping.domain.OrderItem;

@Getter
public class MyPageOrderItemDto {

    private Long id;
    private String itemName;
    private Integer orderPrice;
    private Integer quantity;
    private String calculation; //printCalculate() 결과 담을 필드.

    private Integer itemStock; //남은 재고 (내가 볼려고)


    public MyPageOrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.itemName = orderItem.getItem().getItemName(); //LAZY 초기화.
        this.orderPrice = orderItem.getOrderPrice();
        this.quantity = orderItem.getQuantity();
        this.calculation = orderItem.printCalculate();

        this.itemStock = orderItem.getItem().getQuantity();
    }

    @Override
    public String toString() {
        return "주문 상품 : <br>" +
                "선택된 상품 ='" + itemName + '\'' +
                ", 주문 가격 = " + orderPrice +
                ", 주문 수량 =" + quantity +
                ", 상품 남은 재고량 = " + itemStock +
                '}';
    }
}
