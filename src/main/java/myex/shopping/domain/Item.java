package myex.shopping.domain;

import lombok.Data;

//재고 입장 아이템.
@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price; //가격
    private Integer quantity; //수량(남은재고)
    private String imagePath; //이미지 경로


    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity, String imagePath) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
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
}
