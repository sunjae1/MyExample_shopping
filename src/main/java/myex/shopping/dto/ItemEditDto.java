package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Item;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ItemEditDto {
    private Long id;
    private String itemName;
    private int price;
    private int quantity;
    private String imageUrl;
    private MultipartFile imageFile;

    public ItemEditDto(Item item) {
        this.id = item.getId();
        this.itemName = item.getItemName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
        this.imageUrl = item.getImageUrl();
        this.imageFile = item.getImageFile();
    }
}
