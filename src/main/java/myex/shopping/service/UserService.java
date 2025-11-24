package myex.shopping.service;

import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.User;
import myex.shopping.dto.userdto.UserEditDto;
import myex.shopping.exception.ResourceNotFoundException;
import myex.shopping.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//클래스 레벨에 쓰며 자동 @Bean 등록 및 계층 구분 의미
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    // 빈 스캔해서 타입에 맞는 빈을 찾아 자동으로 DI 연결.
    // 타입 여러 개면 @Primary, @Qualifier 사용해서 우선순위 설정.
    // 하나밖에 없으면 자동 주입됨.
    // DI를 수행하도록 표시 : 생성자, 수정자, 필드 주입. (생성자 하나라면 @Autowired 생략 가능)
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //회원가입 사용자 저장
    //email, name, password 입력.
    @Transactional(readOnly = false)
    public Long save(User user) {
        User savedUser = userRepository.save(user);
        log.info("{}가 저장되었습니다.",savedUser);
        return savedUser.getId();
    }
    //로그인 로직.
    public User login(String email, String password) {
        //반환이 Optional : 1개 담는 컨테이너이기 때문에, 여러 값(컬렉션)을 처리하는 Stream 안쓰고 바로 .filter 가능.
        //현재 : 로그인 실패 시 -> NotFound 404 반환
        // 다시 로그인 페이지로 변경.
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElse(null);
    }
    //전체 사용자 조회
    public List<User> allUser() {
        return userRepository.findAll();
    }

    //회원 정보 수정 (name, email)
    @Transactional(readOnly = false)
    public User updateUser(Long id, UserEditDto updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (updateDTO.getUsername() != null) {
            user.setName(updateDTO.getUsername());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }
        return user;
    }

    //사용자 삭제. -> active로 구분.(soft-delete)
    public void deleteUser(Long id) {

    }
}
