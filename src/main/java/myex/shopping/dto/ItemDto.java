package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Item;

@Getter
public class ItemDto {
    private Long id;
    private String name;
    private int price;
    private int quantity;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.name = item.getItemName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
    }
}
