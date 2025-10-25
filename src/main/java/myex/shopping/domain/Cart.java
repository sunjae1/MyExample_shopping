package myex.shopping.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import myex.shopping.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Getter
@ToString(exclude = {"user", "cartItems"})
@Setter
@Entity
public class Cart {


    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true) //CarItem.cart 가 필드가 연관관계 주인.
    //mappedBy는 연관관계 주인을 가리킴. (저쪽이 주인이야)
    private List<CartItem> cartItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    //장바구니 아이템 추가
    public boolean addItem(Item item, int quantity) {
        //이미 담은 상품이면 수량만 증가
        for (CartItem cartItem : cartItems) {
            if (cartItem.getItem().equals(item)) {

                if (cartItem.getQuantity() + quantity > item.getQuantity())
                {
                    return false;
                }
                else {
                    cartItem.addQuantity(quantity);
                    return true;
                }
            }
        }


//        cartItems.add(new CartItem(item, quantity));
        addCartItem(new CartItem(item, quantity));
        return true;

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


    public void cartItemClear() {
        cartItems.clear();
    }

    //연관관계 편의 메소드
    public void addCartItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    public void deleteCartItem(CartItem cartItem, Item item) {



        cartItems.removeIf(ci -> ci.getItem().equals(item));
        cartItem.setCart(null);
    }
}
