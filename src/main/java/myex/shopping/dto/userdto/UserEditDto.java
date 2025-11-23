package myex.shopping.dto.userdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import myex.shopping.domain.User;

@Getter
@Setter
@Schema(description = "사용자 정보 담는 DTO")
public class UserEditDto {

    @Schema(description = "사용자 이메일", example = "test@na.com")
    private String email;
    @Schema(description = "사용자 이름", example = "테스터")
    private String username;

    public UserEditDto(User user) {
        this.email = user.getEmail();
        this.username = user.getName();
    }

    public UserEditDto() {
    }

    @Override
    public String toString() {
        return "UserDto{" +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
