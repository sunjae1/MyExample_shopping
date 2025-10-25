package myex.shopping.repository.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.User;
import myex.shopping.repository.CartRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCartRepository implements CartRepository {
    private final EntityManager em;

    @Transactional(readOnly = false)
    @Override
    public Cart save(Cart cart) {
        em.persist(cart);
        return cart;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        List<Cart> result = em.createQuery("select c from Cart c left join fetch c.cartItems where c.id= :id", Cart.class)
                .setParameter("id", id)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public Optional<Cart> findByUser(User user) {
        return em.createQuery("select c from Cart c left join fetch c.cartItems where c.user= :user", Cart.class)
                .setParameter("user", user)
                .getResultList()
                .stream().findFirst();
    }

    @Override
    public List<Cart> findAll() {
        return em.createQuery("select distinct c from Cart c " +
                        "join fetch c.cartItems ci", Cart.class)
                .getResultList();
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Long id) {
        Cart cart = em.find(Cart.class, id);
        em.remove(cart);
    }
}
