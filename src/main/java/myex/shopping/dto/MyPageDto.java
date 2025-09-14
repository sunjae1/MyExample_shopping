package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.Cart;
import myex.shopping.domain.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MyPageDto {
    private UserDto user;
    private List<OrderDto> orders;
    private List<PostDto> posts;
    private List<ItemDto> cartItems;

    public MyPageDto(User user, List<OrderDto> orders, List<PostDto> posts, Cart cart) {
        this.user = new UserDto(user);
        this.orders = orders;
        this.posts = posts;
        this.cartItems = cart.getCartItems().stream()
                .map(oi -> new ItemDto(oi.getItem()))
                .collect(Collectors.toList());
    }
}
