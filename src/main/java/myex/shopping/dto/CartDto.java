package myex.shopping.dto;

import lombok.Getter;
import lombok.Setter;
import myex.shopping.domain.Cart;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CartDto {
    private List<CartItemDto> carItems = new ArrayList<>();
    //장바구니 모든 아이템 가격 출력
    private Integer allPrice;

    public CartDto(Cart cart) {
        this.allPrice = cart.allPrice();
        this.carItems = cart.getCartItems().stream()
                .map(CartItemDto::new)
                .collect(Collectors.toList());
    }
}
