package myex.shopping.domain;

import lombok.Data;

@Data
public class Item {

    private Long id;
    private String itemName;
    private Integer price; //가격
    private Integer quantity; //수량
    private String imagePath; //이미지 경로


    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity, String imagePath) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }
}
