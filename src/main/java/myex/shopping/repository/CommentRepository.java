package myex.shopping.repository;

import myex.shopping.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CommentRepository {
    private final Map<Long, Comment> store = new HashMap<>();
    private static Long sequence = 0L;

    public Comment save(Comment comment) {
        comment.setId(++sequence); //저장시키고
        store.put(comment.getId(), comment); //그 아이디를 저장.
        return comment;
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Comment> findAll() {
        return new ArrayList<>(store.values());
    }

    public void delete(Long id) {
        store.remove(id);
    }

    public void clearStore() {
        store.clear();
    }


}
