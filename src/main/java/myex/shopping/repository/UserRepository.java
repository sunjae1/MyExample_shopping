package myex.shopping.repository;

import myex.shopping.domain.User;

import java.util.List;

public interface UserRepository {

    User save(User user);
    User findByEmail(String email);
    User findByName(String name);
    List<User> findAll();

}
