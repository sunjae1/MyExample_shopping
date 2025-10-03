package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.form.CommentForm;
import myex.shopping.form.PostForm;
import myex.shopping.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/posts")
//@Validated //--> BindingResult 전에 예외 터트려서 전역으로 처리하게 함.(Valid + BindingResult 사용 못함.)
//거의 클래스 레벨에서 사용해야 함. 메소드 위에 선언시 객체만 검증 가능.(ModelAttribute, RequestBody)
//메서드 위는 단일 값 검증 RequestParam, PathVariable은 검증 못함.  --> 단일 값 검증은 컨트롤러 안에서 if문으로.
public class PostController {
    private final PostRepository postRepository;


    //게시판 조회.
    @GetMapping
    public String list(Model model,
                       HttpSession session) {
        List<Post> posts = postRepository.findAll();

        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    //게시물 등록 폼
    @GetMapping("/new")
    public String createForm(Model model,
                             HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("user", loginUser);
        model.addAttribute("post", new PostForm());
        return "posts/new";
    }

    //게시물 등록
//<!--post(Form)   :         title, content, userId-->
//    Post(domain) : id(DB), title, content, userId, author, createdDate, comments
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("post") PostForm form,
                         BindingResult bindingResult,
                         Model model,
                         HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (bindingResult.hasErrors()) {
            model.addAttribute("user",loginUser);
            return "posts/new";
        }
        Post post = new Post();
        post.setTitle(form.getTitle());
        post.setContent(form.getContent());
        post.setCreatedDate(LocalDateTime.now());
        post.setUserId(loginUser.getId());
        post.setAuthor(loginUser.getName());

        Post save = postRepository.save(post);
        System.out.println("save = " + save);
        return "redirect:/posts";
    }

    //게시물 한개 보기.
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model, HttpSession session,
                       RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        System.out.println("post.id = " + id);

        //posts/-1 이런 값 넘길때 요청 안받고 다시 리다이렉트로 보냄. //메소드 위 Validated로 불가능.(컨트롤러 안에서 직접 처리)
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorPV","유효하지 않은 게시물입니다.");
            return "redirect:/posts";
        }

        Post post = postOpt.get();


        System.out.println("post = " + post);

        model.addAttribute("loginUser",loginUser);
        model.addAttribute("post", post);
        model.addAttribute("commentForm", new CommentForm());
        return "posts/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) String redirectInfo,
                         RedirectAttributes redirectAttributes) {

        System.out.println(redirectInfo);
//        항상 null 가능성이 있는 변수를 .equals() 앞에 쓰면 NPE 위험

        //삭제할려는 id 가 있는지 검증.
        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorPV", "유효하지 않은 게시물입니다.");
            return "redirect:/posts";
        }


        postRepository.delete(id);
        if ("mypage".equals(redirectInfo))
        {
            return "redirect:/mypage";
        }
        return "redirect:/posts";
    }








}
