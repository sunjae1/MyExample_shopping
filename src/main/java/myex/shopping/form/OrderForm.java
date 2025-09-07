package myex.shopping.form;

import lombok.Data;
import lombok.Getter;

@Data
public class OrderForm {

    //주문 시 받을 정보 : itemId(아이템 조회), 수량 (몇개 주문 했는지)
    private Long id;
    private int quantity;
}
