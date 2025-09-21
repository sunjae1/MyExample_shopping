package myex.shopping.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartForm {

    //주문 시 받을 정보 : itemId(아이템 조회), 수량 (몇개 주문 했는지)
    private Long id;
    @NotNull
    @Min(value = 1)
    private Integer quantity;
}
