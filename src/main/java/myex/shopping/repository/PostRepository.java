package myex.shopping.repository;

import myex.shopping.domain.Post;
import myex.shopping.domain.User;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    Post save (Post post);
    Optional<Post> findById(Long id);
    List<Post> findAll();
    List<Post> findByUser(User user);
    void delete(Long id);

}
