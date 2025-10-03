package myex.shopping.repository.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.PostRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaPostRepository implements PostRepository {

    private final EntityManager em;

    @Transactional(readOnly = false)
    @Override
    public Post save(Post post) {
        em.persist(post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    @Override
    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

    //나중에 Post --> User @ManyToOne 관계로 바꾸기.
    //JPQL :파라미터명(:와 파라미터명은 무조건 붙여야 됨.)
    //=: 파리머터명 (띄어쓰기 오류)

    @Override
    public List<Post> findByUser(User user) {
        return em.createQuery("select p from Post p where p.userId = :user", Post.class)
                .setParameter("user", user.getId())
                .getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(Long id) {
        Post post = em.find(Post.class, id);
        em.remove(post);
    }
}
