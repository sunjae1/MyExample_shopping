package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Item;

@Getter
public class ItemDto {
    private Long id;
    private String itemName;
    private int price;
    private int quantity;
    private String imageUrl;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.itemName = item.getItemName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
        this.imageUrl = item.getImageUrl();
    }
}
