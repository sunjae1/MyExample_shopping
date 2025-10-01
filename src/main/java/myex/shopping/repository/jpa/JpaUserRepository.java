package myex.shopping.repository.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.User;
import myex.shopping.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;

    @Override
    public User save(User user) {
        em.persist(user); //지금 insert문 X, commit 시 insert문 O
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {

        //항상 DB 컬럼명이 아닌, 엔티티 기준으로 작성.

        return em.createQuery("select u from User u where u.email =:email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
        //getSingleResult()는 NoResultException 으로 따로 잡아야 함.
        //getResultStream().findFirst() 결과 없으면 자동으로 Optional.empty()반환.
    }

    @Override
    public User findByName(String name) {
        return em.createQuery("select u from User u where u.name =:name", User.class)
                .setParameter("name", name)
                .getSingleResult();
        //getSingleResult() :조회가 하나일때 사용하는 메소드
        //결과가 0건 NoResultException 발생.
        //결과가 2건 이상 NonUniqueResultException 발생.
    }

    @Override
    public List<User> findAll() {

        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    @Override
    public void clearStore() {

    }
}
