package myex.shopping.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartForm {

    //주문 시 받을 정보 : itemId(아이템 조회), 수량 (몇개 주문 했는지)
    @NotNull(message = "id는 null 일 수 없습니다.")
    private Long id;
    @NotNull(message = "수량을 입력해주세요")
    @Min(value = 1, message = "수량은 1부터 입력 가능합니다.")
    private Integer quantity;

    //price (장바구니 BindingResult 뿌리기 위해서)
    private Integer price;
}
