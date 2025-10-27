package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import myex.shopping.domain.User;

@Getter
@Schema(description = "사용자 정보 담는 DTO")
public class UserDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;
    @Schema(description = "사용자 이메일", example = "test@na.com")
    private String email;
    @Schema(description = "사용자 이름", example = "테스터")
    private String username;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getName();
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
