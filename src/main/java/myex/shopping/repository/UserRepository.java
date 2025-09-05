package myex.shopping.repository;

import myex.shopping.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findByEmail(String email);
    User findByName(String name);
    List<User> findAll();
    void clearStore();
}
