package myex.shopping.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;

@Data
@Entity
@Table(name = "MEMBER") //USER는 H2 예약어. 충돌 남.
public class User {

    @Id @GeneratedValue
    private Long id;

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
