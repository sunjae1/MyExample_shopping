package myex.shopping.repository;

import myex.shopping.domain.Order;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MemoryPostRepository implements PostRepository{

    private Map<Long, Post> store = new HashMap<>();
    private long sequence = 0L;


    @Override
    public Post save(Post post) {
        post.setId(++sequence);
        post.setCreatedDate(LocalDateTime.now());
        store.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    //User 로 Order 전체 찾기. (사용자가 주문한 내역 전부 호출)
    public List<Post> findByUser(User user) {
        return store.values().stream()
                .filter(post -> post.getUserId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    //삭제 버튼
    @Override
    public void delete(Long id) {
        store.remove(id);
    }
}
