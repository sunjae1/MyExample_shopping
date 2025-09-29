package myex.shopping.repository;

import myex.shopping.domain.Item;

import java.util.List;
import java.util.Optional;


public interface ItemRepository {

    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    void update(Long itemId, Item updateParam);
    void deleteItem(Long itemId);

}
