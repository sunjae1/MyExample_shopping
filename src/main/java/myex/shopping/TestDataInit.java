package myex.shopping;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.repository.*;
import myex.shopping.repository.memory.MemoryCommentRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.repository.memory.MemoryOrderRepository;
import org.springframework.stereotype.Component;

@Component //클래스 레벨에서 스프링 빈으로 등록
@RequiredArgsConstructor
public class TestDataInit {

    private final UserRepository userRepository;
    private final MemoryItemRepository memoryItemRepository;
    private final MemoryOrderRepository memoryOrderRepository;
    private final PostRepository postRepository;
    private final MemoryCommentRepository memoryCommentRepository;

    /**
     * 테스트용 데이터 추가
     */
//    @PostConstruct //스프링 빈이 생성되고, 의존성 주입이 끝난 뒤 호출되는 메서드 (빈 이 다 준비되면 자동으로 메소드 실행
    public void init() {
        Item itemA = new Item("아이템A", 2000, 10,"/img/1.webp");
        memoryItemRepository.save(itemA);
        memoryItemRepository.save(new Item("아이템B", 4000,20, "/img/2.webp"));

        User user = new User("test@na.com","테스터","test!");
        userRepository.save(user);

        Integer price = itemA.getPrice();
        int quantity = 3;

        //OrderItem, Order 테스트 데이터 생성.
        OrderItem orderItem = new OrderItem(itemA, price, quantity);
        Order order = new Order(user);
        order.addOrderItem(orderItem);
        memoryOrderRepository.save(order);
        order.confirmOrder();

        //게시글 등록
        Post post = new Post("첫 글 축하", "테스트용 게시글입니다. 게시글 입니다.\n게시글 게시글 게시글");
        post.setUserId(user.getId());
        post.setAuthor(user.getName());
        postRepository.save(post);

        //댓글 등록
        Comment comment = new Comment();
        comment.setUser(user);
//        comment.setPost(post);
        comment.setContent("테스트용 댓글 입력 중입니다. \n테스트 테스트  테스트 테스트 ");
        post.addComment(comment);
        memoryCommentRepository.save(comment);


    }



}
