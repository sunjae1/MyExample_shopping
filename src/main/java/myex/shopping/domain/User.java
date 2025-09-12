package myex.shopping.domain;

import lombok.Data;

import java.util.Objects;

@Data
public class User {
    private Long id;
    private final String email;
    private final String name;
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
