package myex.shopping.repository;

import myex.shopping.domain.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final Map<Long, Order> store = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(0);

    //주문 저장
    public Order save(Order order) {
        long id = sequence.incrementAndGet();
        order.setId(id);
        store.put(id, order);
        return order;
    }

    //id로 찾기
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    //전체 주문 반환.
    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

}
