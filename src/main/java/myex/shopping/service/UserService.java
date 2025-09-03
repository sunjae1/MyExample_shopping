package myex.shopping.service;

import myex.shopping.domain.User;
import myex.shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


//클래스 레벨에 쓰며 자동 @Bean 등록 및 계층 구분 의미(젤로 기능이 없음)
@Service
public class UserService {
    private final UserRepository userRepository;

    // 빈 스캔해서 타입에 맞는 빈을 찾아 자동으로 DI 연결.
    // 타입 여러 개면 @Primary, @Qualifier 사용해서 우선순위 설정.
    // 지금은 하나밖에 없어서 자동 주입됨.
    //DI를 수행하도록 표시 : 생성자, 수정자, 필드 주입. (생성자 하나면 생략 가능)
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //회원가입 user 저장
    //email, name, password 입력.
    public Long save(User user) {
        User savedUser = userRepository.save(user);
        System.out.println(savedUser+"가 저장되었습니다.");
        return savedUser.getId();
    }

    public User login(String email, String password) {
        User byEmail = userRepository.findByEmail(email);

        if (byEmail != null) {
            if (byEmail.getPassword().equals(password)) {
                return byEmail;
            }
        }
        return null;

    }
    public List<User> allUser() {
        return userRepository.findAll();
    }
}
