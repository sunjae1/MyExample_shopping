package myex.shopping.dto;

import lombok.Getter;
import lombok.Setter;
import myex.shopping.domain.CartItem;

@Getter
@Setter
public class CartItemDto {
    private ItemDto item;
    private int quantity;

    public CartItemDto(CartItem cartItem) {
        this.quantity = cartItem.getQuantity();
        this.item = new ItemDto(cartItem.getItem());
    }
}
