package myex.shopping.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.CartItem;
import myex.shopping.domain.Item;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final EntityManager em;
    
    @Transactional(readOnly = false)
    public void deleteItem (Long itemId, HttpSession session) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 아이템은 존재 하지 않습니다."));
        Cart cart = getOrCreateCart(session);
        Cart findCart = cartRepository.findById(cart.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장바구니는 존재 하지 않습니다."));



        findCart.removeItem(item); //DB 더디 체킹으로 없애고.

        //객체도 세션이라 같이 없앰.
        cart.removeItem(item);
        //더티 체킹 - delete쿼리 날아가게.
    }



   @Transactional
    public void update(long cartId, Cart cart) {
        Optional<Cart> byId = cartRepository.findById(cartId);
        Cart findCart = byId.get();
       System.out.println("DBCart = " + findCart);
       System.out.println("SessionCart = " + cart);


//        findCart.setCartItems(cart.getCartItems());
       Cart managedCart = em.merge(cart);//cart 상태 managedCart.getId 이런걸로 최신 상태로 만들어야함.
       em.flush(); //id는 hibernate가 merge()호출 중에 IdentifierGenerator 로 바로 ID 생성함. (다음 줄에 바로 Native Query가 나가지 않는 이상 지금 상태에선 필요 없음)

       //세션과 영속 객체 같게 맞춤.
      cart.setId(managedCart.getId());
      cart.getCartItems().clear();

       for (CartItem managedCI : managedCart.getCartItems()) {
           CartItem sessionCI = new CartItem();
           sessionCI.setId(managedCI.getId());
           sessionCI.setItem(managedCI.getItem());
           sessionCI.setQuantity(managedCI.getQuantity());

           //연관관계 주의 : cart는 위에 기존 세션 cart로 설정
           sessionCI.setCart(cart);
           cart.getCartItems().add(sessionCI);
       }



       System.out.println("managedCart = " + managedCart);
       System.out.println("SessionCart = " + cart);

    }


    //메소드
    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            cart = new Cart();
//            cart.setId(1L);
            session.setAttribute("CART", cart);
        }
        return cart;
    }
}
