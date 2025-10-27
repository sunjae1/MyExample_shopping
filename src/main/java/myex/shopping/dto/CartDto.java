package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import myex.shopping.domain.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Schema(description = "장바구니 정보 담는 DTO")
public class CartDto {
    @Schema(description = "장바구니 상품 담는 DTO", example = "[CartItem1, CartItem2]")
    private List<CartItemDto> carItems = new ArrayList<>();
    @Schema(description = "장바구니 전체 아이템 가격", example = "6000")
    //장바구니 모든 아이템 가격 출력
    private Integer allPrice;

    public CartDto(Cart cart) {
        this.allPrice = cart.allPrice();
        this.carItems = cart.getCartItems().stream()
                .map(CartItemDto::new)
                .collect(Collectors.toList());
    }
}
