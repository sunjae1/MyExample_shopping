package myex.shopping.domain;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Data
public class User {

    private Long id;

    //현재 Register에 사용중.

    @NotEmpty(message = "이메일을 입력해주세요")
    private final String email;
    @NotEmpty(message = "사용자 이름을 입력해주세요")
    private final String name;
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Size(min = 3, max = 15, message = "패스워드는 3자 이상 15자 이하 입니다.")
    private final String password;


    @Override
    public String toString() {
        return "User{" + "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
