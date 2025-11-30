package myex.shopping.repository;

import myex.shopping.domain.Item;
import myex.shopping.dto.itemdto.ItemDto;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public interface ItemRepository {

    Item save(Item item);
    Optional<Item> findById(Long id);
    List<Item> findAll();
    void update(Long itemId, Item updateParam);
    void deleteItem(Long itemId);

    List<Item> searchByName(String keyword);
}
