package myex.shopping.dto;

import myex.shopping.domain.OrderItem;

public class OrderItemDto {
    private String itemName;
    private int price;
    private int quantity;

    public OrderItemDto(OrderItem oi) {
        this.itemName = oi.getItem().getItemName();
        this.price = oi.getItem().getPrice();
        this.quantity = oi.getQuantity();
    }
}
