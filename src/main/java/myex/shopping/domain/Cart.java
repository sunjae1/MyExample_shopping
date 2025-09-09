package myex.shopping.domain;

import java.util.ArrayList;
import java.util.List;



public class Cart {

    private List<CartItem> cartItems = new ArrayList<>();

    //장바구니 아이템 추가
    public void addItem(Item item, int quantity) {
        //이미 담은 상품이면 수량만 증가
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().equals(item)) {
                cartItem.addQuantity(quantity);
                return;
            }
        }

        cartItems.add(new CartItem(item, quantity));

    }


    //장바구니 모든 아이템 가격 출력
    public int allPrice() {
        int allPrice =0;
        for (CartItem cartItem : cartItems) {
            allPrice += cartItem.getItem().getPrice() * cartItem.getQuantity();
        }
        return allPrice;

    }

    //장바구니 들어가서 취소하기 버튼 만들기.
    public void removeItem(Item item) {
        cartItems.removeIf(cartItem -> cartItem.getItem().equals(item));
    }


    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void cartItemClear() {
        cartItems.clear();
    }
}
