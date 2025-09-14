package myex.shopping.dto;

import lombok.Getter;
import myex.shopping.domain.User;

@Getter
public class UserDto {
    private Long id;
    private String email;
    private String username;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getName();
    }
}
