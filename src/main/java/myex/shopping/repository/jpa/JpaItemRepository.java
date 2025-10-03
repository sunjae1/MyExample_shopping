package myex.shopping.repository.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.repository.ItemRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = false)
    public Item save(Item item) {
        //persist : "새로운 엔티티" 라고 생각. 이때, id == null 이면 INSERT 쿼리,
        // id 가 값이 있으면, "준영속 객체인데 persist 하려 한다." -> PersistentObjectException 예외 발생.
        //@GeneratedValue 없이, setId() 하는데 중복 되면, PK 중복 ConstraintViolationException 발생.
        em.persist(item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(em.find(Item.class, id));
    }

    @Override
    public List<Item> findAll() {

        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Long itemId, Item updateParam) {
        Item item = new Item(); //비영속
        item.setId(itemId);
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
        item.setImageUrl(updateParam.getImageUrl());
        //DB에 파일은 저장 못하기 때문에 Url 만 저장.

        //비영속 -> 영속 객체 생성해서 영속성 컨텍스트에 등록.
        //여기서 item은 여전히 비영속, em.merge 반환값이 영속 객체.
        //commit, flush 일어나면 INSERT, UPDATE 쿼리 나감.
        em.merge(item);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteItem(Long itemId) {
        //em.remove는 영속 객체를 매개변수로 받아야함.
        //entity -> em.remove(entity) /람다를 더 짧게 쓴 것.
        Optional<Item> findOpt = findById(itemId);
        findOpt.ifPresent(em::remove);
    }
}
