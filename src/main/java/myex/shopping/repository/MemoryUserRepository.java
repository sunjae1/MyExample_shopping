package myex.shopping.repository;

import myex.shopping.domain.User;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Bean 등록이랑 DB 접근 예외를 스프링 예외로 변환하는 기능 가짐.
@Repository
public class MemoryUserRepository implements UserRepository{

    //@Repository 이런 어노테이션에 다 싱글톤 보장 : 해당 클래스의 객체가 딱 하나만 만들어지도록. / 근데 예제에서 명시적으로 private static 처럼 싱글톤 흉내내기 위해.
    private static HashMap<Long, User> map = new HashMap<>();
    private static Long sequence = 0L;



    @Override
    public User save(User user) {
        user.setId(++sequence);
        map.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {

        Collection<User> values = map.values();

        //두개 합쳐서 향상된 for문 가능.(이것도 iterator를 쓰기 때문에 구현 되있어야함).
        Iterator<User> iterator = values.iterator();
        while(iterator.hasNext())
        {
            User next = iterator.next();
            if (next.getEmail().equals(email)) {
                return Optional.of(next);
            }
        }
        return Optional.empty();

    /*    Collection<User> value2 = map.values();
        for (User user : value2) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }*/



    }

    @Override
    public User findByName(String name) {
        Optional<User> findUser = map.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst();
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public void clearStore() {
        map.clear();
    }
}
