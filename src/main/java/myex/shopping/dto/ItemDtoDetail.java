package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Item;

@Getter
public class ItemDtoDetail {
    private Long id;
    private String name;
    private int price;
    private int quantity;
    private String imageUrl;

    public ItemDtoDetail(Item item) {
        this.id = item.getId();
        this.name = item.getItemName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
        this.imageUrl = item.getImageUrl();
    }
}
