package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import myex.shopping.repository.PostRepository;
import myex.shopping.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Post addUser(Post post,User user) {

        User persistUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        post.addUser(persistUser);
        postRepository.save(post);
        return post;

    }

}
