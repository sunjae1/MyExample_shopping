package myex.shopping.domain;

import lombok.Data;

@Data
public class CartItem {

    //아이템과 수량만 기록
    private Item item;
    private int quantity;

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public int totalItemPrice() {
        return item.getPrice() * quantity;
    }


}
