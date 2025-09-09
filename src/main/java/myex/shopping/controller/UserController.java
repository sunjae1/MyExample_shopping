package myex.shopping.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.domain.User;
import myex.shopping.repository.ItemRepository;
import myex.shopping.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

//@Bean은 메소드 레벨(개발자가 직접 반환 객체를 빈으로 등록), @Component는 클래스 레벨
//@Configuration : Bean 메소드 모아놓고, Bean 메서드 싱글톤 보장

//자동 @Bean 등록이랑 HttpServlet request url parsing 이랑 handler 연결하고 viewResolver 와 HttpServlet Response 로 등등 여러 역할 수행하는 어노테이션(젤 기능 많음)
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ItemRepository itemRepository; //생성자 주입.





    @GetMapping("/")
    public String start() {
        return "login";

    }
    @PostMapping("/")
    public String login(@RequestParam("email")String email,
                        @RequestParam("password")String password,
                        Model model,
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
        User loginUser = userService.login(email, password);
        System.out.println(loginUser+" login에 성공했습니다.(userService.login 메소드 통과함)");

        if (loginUser == null) {
            System.out.println("아이디나 비밀번호가 틀렸습니다. ");
            model.addAttribute("errorMessage", "아이디나 비밀번호가 틀렸습니다."); //뷰로 한번 가면 사라짐. PRG 패턴에서 저장 안됨.
            redirectAttributes.addFlashAttribute("errorMessage","아이디나 비밀번호가 틀렸습니다.");
            //세션 잠깐 활용. 리다이렉트 이후 새 요청에서 1번만 사용가능 URL 노출 안됨.
            return "redirect:/";
        }

        //로그인 성공 로직 (UUID 해서 있으면 그냥 반환, 아니면 신규 세션 생성.
        HttpSession session = request.getSession();
        session.setAttribute("loginUser", loginUser);



        return "redirect:/main";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String add_user(@ModelAttribute("user")User user) {
        //User user = new User();
        //user.setEmail, setName, setPassword
        //model.addAttribute("user",user) : 뷰에 정보 뿌릴때 사용.
        //ModelAttribute가 자동으로 생성해줌.
        userService.save(user);

        return "redirect:/";
    }

    @GetMapping("/allUser")
    public String allUser(Model model) {
        //controller --> service --> repository
        //return : List 로 받은 User 목록들 th:each로 루프 돌리기.
        List<User> users = userService.allUser();
        model.addAttribute("users", users);
        return "allUser";
    }

    @GetMapping("/main")
    public String main(Model model,HttpServletRequest request) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);

        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("user",loginUser);

        return "main";
    }

}
