package myex.shopping.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

//재고 입장 아이템.
@Data
@Entity
public class Item {

    @Id
    @GeneratedValue
    private Long id;
    private String itemName;
    private Integer price; //가격
    private Integer quantity; //수량(남은재고)

    @Transient //JPA가 컬럼으로 만들지 않음 :업로드 파일은 서버에 저장/DB에는 경로만 저장.
    private MultipartFile imageFile; //업로드 파일
    private String imageUrl; //이미지 경로


    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity, String imageUrl) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }


    public void decreaseStock(int quantity) {
        int decreasedQuantity = this.quantity - quantity;
        if (decreasedQuantity <0)
            throw new RuntimeException("not enough stock");
        this.quantity = decreasedQuantity;
    }

    public void increaseStock(int quantity) {
        this.quantity += quantity;
    }


    @Override
    public String toString() {
        return "선택된 상품 ={'" + itemName + '\'' +
                ", 가격=" + price +
                ", 수량=" + quantity +
                '}';
    }
}
