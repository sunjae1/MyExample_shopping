package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.dto.*;
import myex.shopping.form.RegisterForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.OrderRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.repository.memory.MemoryOrderRepository;
import myex.shopping.repository.PostRepository;
import myex.shopping.repository.UserRepository;
import myex.shopping.service.CartService;
import myex.shopping.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//API 컨트롤러 에서는 뷰를 리턴하지 않고, JSON + 상태 코드만 리턴함.
//어디로 이동해야 할지는 서버가 아닌 클라이언트(React, Vue, Angular, 모바일 앱 등)가 결정.

//POST/PUT/PATCH/DELETE(등록, 수정, 삭제) : 성공/실패 여부 따라
//201 CREATED, 204 NO CONTENT, 400 BAD REQUEST, 404 NOT FOUND 등
//다양한 상태 코드 필요. --> ResponseEntity<DTO> 반환.

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "User", description = "사용자 관련 API") //Open API Spec 설명.
@Validated // 선택 사항 : @RequestParam, @PathVariable 검증 시 필요.
//DTO는 @Valid만 매개변수 앞에 붙여도 검증 가능.
public class ApiUserController {

    //프론트엔드에서 뷰를 뿌리고 Api 에서는, 프론트에서 오는 정보를 서버에 거쳐 처리하고 다시 JSON 으로 반환. (뷰 필요없어서 GetMapping 거의 다 제거됨.)

    //생성자 주입.
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PostRepository postRepository;

    private final UserService userService;
    private final CartService cartService;


    @Operation(
            summary = "로그인 성공",
            description = "ID와 PW를 입력해 로그인을 처리한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인 인증 실패")
            }
    )
    //로그인
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpSession session) {


        User loginUser = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        System.out.println(loginUser+" login에 성공했습니다.(userService.login 메소드 통과함)");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //로그인 실패, 인증 실패
        }
        //로그인 성공 로직 (UUID 사용하여 있으면 반환, 아니면 신규 세션 생성)
        session.setAttribute("loginUser", loginUser);
        return ResponseEntity.ok(new UserDto(loginUser)); //200 OK
    }

    @Operation(
            summary = "회원가입",
            description = "회원가입 등록",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 등록 성공")
            }
    )
    //회원가입 입력값 : email, name, password
    @PostMapping("/register")
    public ResponseEntity<UserDto> add_user(@Valid @RequestBody RegisterForm form) {
        User user = new User(form.getEmail(),form.getName(), form.getPassword());
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserDto(user));

    }

    @Operation(
            summary = "전체 사용자 조회",
            description = "관리자가 전체 사용자를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",description = "조회 성공")}
    )
    //전체 사용자 조회
    @GetMapping("/allUser")
    public ResponseEntity<List<UserDto>> allUser(Model model) {
        return ResponseEntity.ok(
                userService.allUser().stream()
                .map(UserDto::new)
                .collect(Collectors.toList()));
    }

    @Operation(
            summary = "메인페이지(쇼핑몰), 전체 상품 조회",
            description = "메인페이지로 들어가서 전체 상품을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전체 상품 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인 실패(계정 정보 없음(세션))")
            }
    )
    //메인 페이지, 상품 전체 조회. (record 확인 한번 하기)
    @GetMapping("/main")
    public ResponseEntity<UserResponse> mainPage(HttpSession session) {
        List<ItemDto> itemDto = itemRepository.findAll().stream()
                .map(ItemDto::new)
                .collect(Collectors.toList());

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //계정 정보 없음(세션)
        }
        UserDto userDto = new UserDto(loginUser);
        return ResponseEntity.ok(new UserResponse(userDto, itemDto)); //200 OK
    }


    @Operation(
            summary = "마이 페이지 요청",
            description = "사용자의 Cart, Order, Post 를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "401", description = "로그인 실패"),
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    //마이페이지 보내는거 : user, orders, posts, cart
    @GetMapping("/myPage")
    public ResponseEntity<MyPageDto> myPage(HttpSession session)
    {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); //401 로그인 실패
        }

        List<OrderDto> orders = orderRepository.findByUser(loginUser).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        List<PostDto> posts = postRepository.findByUser(loginUser).stream()
                .map(PostDto::new)
                .collect(Collectors.toList());

        //브라우저 세션이랑 포스트맨 세션이랑 다름.
        //브라우저에서 카트에 넣어도, 포스트맨에서 cart가 세션이라 공유 불가.
//        Cart cart = getOrCreateCart(session);
        //Cart : 세션 -> DB로 교체.
        Cart cart = cartService.findOrCreateCartForUser(loginUser);

        System.out.println("orders = " + orders);
        System.out.println("cart = " + cart);


        return ResponseEntity.ok(new MyPageDto(loginUser, orders, posts, cart));
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
