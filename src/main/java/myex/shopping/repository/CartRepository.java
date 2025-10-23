package myex.shopping.repository;

import myex.shopping.domain.Cart;
import myex.shopping.domain.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartRepository {
    Cart save (Cart cart);
    Optional<Cart> findById(Long id);
    List<Cart> findAll();
    void delete(Long id);
}
