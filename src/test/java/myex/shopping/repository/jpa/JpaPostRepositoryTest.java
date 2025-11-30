package myex.shopping.repository.jpa;

import myex.shopping.domain.Comment;
import myex.shopping.domain.Post;
import myex.shopping.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaPostRepository.class)
class JpaPostRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaPostRepository postRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("postuser@example.com", "postuser", "password");
        em.persistAndFlush(user);
        em.clear();
    }

    @Test
    @DisplayName("새로운 게시물 저장 테스트")
    void save() {
        // given
        User managedUser = em.find(User.class, user.getId());
        Post post = new Post("New Post", "Content of new post");
        post.setUser(managedUser);

        // when
        Post savedPost = postRepository.save(post);
        em.flush();
        em.clear();

        // then
        assertThat(savedPost.getId()).isNotNull();
        Post foundPost = em.find(Post.class, savedPost.getId());
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getTitle()).isEqualTo("New Post");
        assertThat(foundPost.getUser().getId()).isEqualTo(managedUser.getId());
    }

    @Test
    @DisplayName("ID로 게시물 조회")
    void findById() {
        // given
        User managedUser = em.find(User.class, user.getId());
        Post post = new Post("Find Me Post", "Content");
        post.setUser(managedUser);
        em.persistAndFlush(post);
        Long postId = post.getId();
        em.clear();

        // when
        Optional<Post> foundPostOpt = postRepository.findById(postId);

        // then
        assertThat(foundPostOpt).isPresent();
        assertThat(foundPostOpt.get().getId()).isEqualTo(postId);
    }

    @Test
    @DisplayName("사용자로 게시물 목록 조회")
    void findByUser() {
        // given
        User managedUser = em.find(User.class, user.getId());
        Post post1 = new Post("Post 1", "Content 1");
        post1.setUser(managedUser);
        em.persist(post1);

        Post post2 = new Post("Post 2", "Content 2");
        post2.setUser(managedUser);
        em.persist(post2);

        em.flush();
        em.clear();

        // when
        User queryUser = em.find(User.class, managedUser.getId());
        List<Post> posts = postRepository.findByUser(queryUser);

        // then
        assertThat(posts).hasSize(2);
    }

    @Test
    @DisplayName("모든 게시물 조회 (연관된 사용자, 댓글과 함께)")
    void findAll() {
        // given
        User managedUser = em.find(User.class, user.getId());
        Post post1 = new Post("Post 1", "Content 1");
        post1.setUser(managedUser);

        Comment comment1 = new Comment();
        comment1.setContent("Comment for Post 1");
        comment1.setUser(managedUser);
        post1.addComment(comment1);
        em.persist(post1);

        User user2 = new User("user2@example.com", "user2", "password123");
        em.persist(user2);
        Post post2 = new Post("Post 2", "Content 2");
        post2.setUser(user2);
        em.persist(post2);
        
        em.flush();
        em.clear();

        // when
        List<Post> allPosts = postRepository.findAll();

        // then
        assertThat(allPosts).hasSize(2);
        // Verify that fetching is working by accessing related entities
        assertThat(allPosts.get(0).getUser()).isNotNull();
        // The post with a comment should have its comments loaded
        Post foundPost1 = allPosts.stream().filter(p -> p.getId().equals(post1.getId())).findFirst().get();
        assertThat(foundPost1.getComments()).hasSize(1);
    }

    @Test
    @DisplayName("게시물 삭제 테스트")
    void delete() {
        // given
        User managedUser = em.find(User.class, user.getId());
        Post post = new Post("To be deleted", "Delete me");
        post.setUser(managedUser);
        em.persistAndFlush(post);
        Long postId = post.getId();
        em.clear();

        // when
        postRepository.deleteById(postId);
        em.flush();
        em.clear();

        // then
        Post deletedPost = em.find(Post.class, postId);
        assertThat(deletedPost).isNull();
    }
}
