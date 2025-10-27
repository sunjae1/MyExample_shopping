package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//@Data
@Getter
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @NotBlank(message = "이메일은 필수 입니다.")
    @Schema(description = "사용자 이메일", example = "test@na.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    @Schema(description = "사용자 비밀번호", example = "test1231!@")
    private String password;
}
