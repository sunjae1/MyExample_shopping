package myex.shopping.domain;

import lombok.Data;

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
}
