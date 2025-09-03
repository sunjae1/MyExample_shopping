package myex.shopping.repository;

import myex.shopping.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
    public User findByEmail(String email) {

        List<User> alluser = findAll();
        for (User user : alluser) {
            if (user.getEmail().equals(email)){
                return user;
            }
            
        }
        
        return null;
    }

    @Override
    public User findByName(String name) {
        List<User> alluser = findAll();

        //Java Iterator 사용해보기
        Iterator<User> iterator = alluser.iterator();
        System.out.println("iterator = " + iterator.getClass());
        while (iterator.hasNext())
        {
            if (iterator.next().getName().equals(name))
                return iterator.next();
        }
        
        return null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(map.values());
    }
}
