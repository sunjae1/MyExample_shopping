package myex.shopping.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LoginForm {

    @NotEmpty(message = "사용자 이메일은 필수 입니다.")
    @Email
    private String email;

    //@Range 는 Long, Integer 같은 Number 범위를 검증하기 위한 것.
    //@Size 는 문자열 검증
    @NotEmpty(message = "비밀번호는 필수입니다.")
    @Size(min = 3, max = 15, message = "비밀번호는 3자 이상 15자 이하 입니다.")
    private String password;
}
