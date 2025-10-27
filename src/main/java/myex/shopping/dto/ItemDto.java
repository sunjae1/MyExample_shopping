package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import myex.shopping.domain.Item;

@Getter
@Schema(description = "상품 정보를 담는 DTO")
public class ItemDto {
    @Schema(description = "상품ID", example = "1")
    private Long id;
    @Schema(description = "상품이름", example = "아이템A")
    private String itemName;
    @Schema(description = "상품가격", example = "2000")
    private int price;
    @Schema(description = "상품재고 수량", example = "30")
    private int quantity;
    @Schema(description = "상품 이미지 URL", example = "*/img/1.webp")
    private String imageUrl;

    public ItemDto(Item item) {
        this.id = item.getId();
        this.itemName = item.getItemName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
        this.imageUrl = item.getImageUrl();
    }
}
