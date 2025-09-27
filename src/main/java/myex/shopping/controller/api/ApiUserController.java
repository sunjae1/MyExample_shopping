package myex.shopping.controller.api;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.dto.*;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.repository.PostRepository;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.UserService;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

//API 컨트롤러 에서는 뷰를 리턴하지 않고, JSON + 상태 코드만 리턴함.
//어디로 이동해야 할지는 서버가 아닌 클라이언트(React, Vue, Angular, 모바일 앱 등)가 결정해야한다.

//GET(단순 조회)는 200 OK 고정이라 --> DTO만 반환하는 경우 많다.
//POST/PUT/PATCH/DELETE(등록, 수정, 삭제) : 성공/실패 여부 따라
//201 CREATED, 204 NO CONTENT, 400 BAD REQUEST, 404 NOT FOUND 등 다양한 상태 코드 필요. --> ResponseEntity<DTO> 반환.

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated // 선택 사항 : @RequestParam, @PathVariable 검증 시 필요.
//DTO는 그냥 @Valid 만 매개변수 앞에 붙여도 검증 가능.
public class ApiUserController {

    //Api 에서는 프론트엔드에서 뷰를 뿌리고, 프론트에서 오는 정보를 서버를 거쳐 처리하고 다시 JSON 으로 반환. (뷰 필요없어서 GetMapping 거의 다 사라짐.)

    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository; //생성자 주입.
    private final OrderRepository orderRepository;
    private final PostRepository postRepository;


    //로그인
    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                        HttpSession session) {


        User loginUser = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        System.out.println(loginUser+" login에 성공했습니다.(userService.login 메소드 통과함)");
        if (loginUser == null) {
            throw new RuntimeException("아이디 또는 비밀번호 오류입니다.");
        }
        //로그인 성공 로직 (UUID 해서 있으면 그냥 반환, 아니면 신규 세션 생성.
        session.setAttribute("loginUser", loginUser);
        return new UserDto(loginUser);
    }

    //회원가입 email, name, password 날아옴
    @PostMapping("/register")
    public UserDto add_user(@RequestBody User user) {
        Long saveUserId = userService.save(user);
        return new UserDto(user);
    }

    //전체 사용자 조회
    @GetMapping("/allUser")
    public List<UserDto> allUser(Model model) {

        return userService.allUser().stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    //메인 페이지, 상품 전체 조회. (record 확인 한번 하기)
    @GetMapping("/main")
    public UserResponse mainPage(HttpSession session) {
        List<ItemDto> itemDto = itemRepository.findAll().stream()
                .map(ItemDto::new)
                .collect(Collectors.toList());

        User loginUser = (User) session.getAttribute("loginUser");
        UserDto userDto = new UserDto(loginUser);
        return new UserResponse(userDto, itemDto);
    }


    //마이페이지 보내는거 : user, orders, posts, cart : 아직 오류남.
    @GetMapping("/myPage")
    public MyPageDto myPage(HttpSession session)
    {
        User loginUser = (User) session.getAttribute("loginUser");
        List<OrderDto> orders = orderRepository.findByUser(loginUser).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        List<PostDto> posts = postRepository.findByUser(loginUser).stream()
                .map(PostDto::new)
                .collect(Collectors.toList());

        //브라우저 세션이랑 포스트맨 세션이랑 다름.
        //브라우저에서 카트에 넣어도, 포스트맨에서 cart가 세션이라 못받음.
        Cart cart = getOrCreateCart(session);

        System.out.println("orders = " + orders);
        System.out.println("cart = " + cart);


        return new MyPageDto(loginUser, orders, posts, cart);
    }



    private static Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("CART", cart);
        }
        return cart;
    }

}
