package myex.shopping.repository;

import myex.shopping.domain.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    //@Repository 하면 스프링이 클래스 단위로 싱글톤 보장 해줘서, 멤버변수에 static 할 필요 없음.
    private static Long sequence = 0L;
    private static final Map<Long, Item> store = new HashMap<>();

    //상품 저장
    public Item save(Item item) {

        item.setId(++sequence);
        store.put(sequence, item);
        return item;
    }
    //상품 조회(id, key 로 조회)
    public Item findById(Long id) {
        return store.get(id);
    }

    //전체 상품 조회
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    //업데이트 처리(이름, 가격, 재고 수정)
    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());

    }

    //item 저장소 전부 삭제.
    public void clearStore() {
        store.clear();
    }


}
