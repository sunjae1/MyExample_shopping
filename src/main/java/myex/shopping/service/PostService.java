package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.dto.dbdto.MyPagePostDBDto;
import myex.shopping.dto.dbdto.PostDBDto;
import myex.shopping.repository.PostRepository;
import myex.shopping.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post addUser(Post post,Long userId) {

        User persistUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        post.addUser(persistUser);
        postRepository.save(post);
        return post;
    }
    public PostDBDto changeToDto(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시물은 존재 하지 않습니다."));
        return new PostDBDto(post); //트랜잭션 내 user, comments 접근
    }

    public List<MyPagePostDBDto> changeToDtoList(User user) {
        return postRepository.findByUser(user)
                .stream()
                .map(MyPagePostDBDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostDBDto> findAllPostDBDto() {
        //1. fetch join으로 최적화 된 finAll()호출(쿼리 1번, Post - User, Comments)
        List<Post> posts = postRepository.findAll();

        return posts.stream()
                .map(PostDBDto::new) //post -> new PostDBDto(post) 동일.
                .collect(Collectors.toList());

    }

}
