package myex.shopping.service;

import myex.shopping.domain.User;
import myex.shopping.repository.memory.MemoryUserRepository;
import myex.shopping.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

//단위 테스트 목적. DI로 스프링 애플리케이션이 넣는게 아니라,(@SpringTest, 통합) 직접 자바 코드로 직접 주입하고 직접 다 해보는거. 스프링 없이.
class UserServiceTest {

    private final UserRepository userRepository = new MemoryUserRepository();
    UserService userService = new UserService(userRepository);

    //테스트 하나 끝나고 DB 초기화.
    @AfterEach
    public void afterEach() {
        userRepository.clearStore();
    }


    @Test
    void findByEmailTest () {
        userRepository.save(new User("aa@na.com","홍길동","aaa"));
        userRepository.save(new User("bb@na.com","전우치","aaa"));

        Optional<User> findUser = userRepository.findByEmail("aa@na.com");
        if (findUser.isPresent())
        {
            User user = findUser.get();
            String email = user.getEmail();
            Assertions.assertThat(email).isEqualTo("aa@na.com");

        }

    }

    @Test
    void login() {
        userRepository.save(new User("aa@na.com","길동홍","aa"));
        userRepository.save(new User("bb@na.com","길홍","bb"));
        User loginUser = userService.login("aa@na.com", "aa");

        System.out.println("로그인이 성공했습니다."+ loginUser);

    }

}