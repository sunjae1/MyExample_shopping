package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import myex.shopping.domain.Item;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Schema(description = "상품 수정 정보 담는 DTO")
public class ItemEditDto {
    @Schema(description = "상품ID", example = "1")
    private Long id;
    @Schema(description = "상품이름", example = "아이템A")
    private String itemName;
    @Schema(description = "상품가격", example = "2000")
    private int price;
    @Schema(description = "상품재고 수량", example = "30")
    private int quantity;
    @Schema(description = "상품 이미지 URL",example = "*/img/1.webp")
    private String imageUrl;
    @Schema(description = "상품 파일(바이너리 데이터)")
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
