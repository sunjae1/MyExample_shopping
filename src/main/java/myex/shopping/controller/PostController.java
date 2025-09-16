package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/posts")
public class PostController {
    private final PostRepository postRepository;


    //게시판 조회.
    @GetMapping
    public String list(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    //게시물 등록 폼
    @GetMapping("/new")
    public String createForm(Model model,
                             HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        model.addAttribute("user", loginUser);
        model.addAttribute("post", new Post());
        return "posts/new";
    }

    //게시물 등록
//<!--post(Form)   :         title, content, userId-->
//    Post(domain) : id(DB), title, content, userId, author, createdDate, comments
    @PostMapping
    public String create(@ModelAttribute Post post,
                         HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        post.setUserId(loginUser.getId());
        post.setAuthor(loginUser.getName());

        Post save = postRepository.save(post);
        System.out.println("save = " + save);
        return "redirect:/posts";
    }

    //게시물 한개 보기.
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {

        System.out.println("id = " + id);
        Post post = postRepository.findById(id)
                .orElse(new Post());

        System.out.println("post = " + post);

        model.addAttribute("post", post);
        return "posts/view";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) String redirectInfo) {

        System.out.println(redirectInfo);
//        항상 null 가능성이 있는 변수를 .equals() 앞에 쓰면 NPE 위험
        postRepository.delete(id);
        if ("mypage".equals(redirectInfo))
        {
            return "redirect:/mypage";
        }
        return "redirect:/posts";
    }








}
