package myex.shopping.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterForm {
    @NotEmpty(message = "사용자 이름은 필수 입니다.")
    private String name;
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email
    private String email;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Size(min = 3, max = 15, message = "비밀번호는 3자 이상 15자 이하 입니다.")
    private String password;
}
