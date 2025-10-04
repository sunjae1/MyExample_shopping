package myex.shopping;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.*;
import myex.shopping.repository.*;
import myex.shopping.repository.memory.MemoryCommentRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.repository.memory.MemoryOrderRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component //클래스 레벨에서 스프링 빈으로 등록
@RequiredArgsConstructor
public class TestDataInit {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 테스트용 데이터 추가
     */
    /*
    프록시로 감싸지고 트랜잭션을 시작/종료하는데,
    @PostConstruct는 프록시로 감싸지지기 전 원본 객체에서 호출 될 수 있다. -> 트랜잭션 적용 안됨. -> save(itemA) 는 한줄만 Repository 에서 트랜잭션 적용되고, 다시 여기 코드에선 준영속으로 됨.(Transactional이 없기 때문에 대출 된 책은 준영속)
    --Spring Application 완전히 초기화 되고, 모든 Bean, Proxy 준비 된 후에 데이터 초기화 시킬려면 @PostConstruct --> @ApplicationReadyEvent 써야함. --
    @PostConstruct //스프링 빈이 생성되고, 의존성 주입이 끝난 뒤 호출되는 메서드 (빈이 다 준비되면 자동으로 메소드 실행*/
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        //전부 save -> em.persist 되어서, 자바 객체 인스턴스 자체가 영속성으로 관리되어 @Transactional 안붙여도 의도대로 동작함.

        Item itemA = new Item("아이템A", 2000, 10,"/img/1.webp");
        itemRepository.save(itemA);
        itemRepository.save(new Item("아이템B", 4000,20, "/img/2.webp"));

        User user = new User("test@na.com","테스터","test!");
        userRepository.save(user);

        Integer price = itemA.getPrice();
        int quantity = 3;

        //OrderItem, Order 테스트 데이터 생성.
        OrderItem orderItem = new OrderItem(itemA, price, quantity);
        Order order = new Order(user);
        order.addOrderItem(orderItem);
        orderRepository.save(order);
        order.confirmOrder();

        //게시글 등록
        Post post = new Post("첫 글 축하", "테스트용 게시글입니다. 게시글 입니다.\n게시글 게시글 게시글");
        post.setUserId(user.getId());
        post.setAuthor(user.getName());
        post.setCreatedDate(LocalDateTime.now());
        postRepository.save(post);

        //댓글 등록
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setContent("테스트용 댓글 입력 중입니다. \n테스트 테스트  테스트 테스트 ");
        post.addComment(comment);
        commentRepository.save(comment);
    }
}
