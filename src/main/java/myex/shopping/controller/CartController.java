package myex.shopping.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.form.CartForm;
import myex.shopping.repository.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class CartController {
    private final ItemRepository itemRepository;

    //한 상품에 대한 주문 페이지를 보여주고, 장바구니 담기 클릭시 장바구니에 저장.
    //@PathVariable은 itemid로 item 꺼내기 위해서 get에서 뽑아서 뷰에 item 뿌려줌.
    //main --> 장바구니 담기 버튼.
    @GetMapping("/{itemId}/cart")
    public String viewCart(@PathVariable Long itemId,
                           HttpSession session, Model model,
                           @ModelAttribute CartForm cartForm) {

        System.out.println("cartForm = " + cartForm);

        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "cart/cartForm"; // 주문이랑 똑같이 view에서 받고, 전체 장바구니에서 주문 버튼 만들어서, order.confirmOrder 을 나중으로 미루게.(확정을 나중으로)

    }

    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    // id, price, quantity --> CartForm : id, quantity 매핑.
    //@ModelAttribute는 원래 첫글자 소문자 + 원글 cartFrom 으로 model에 저장함.
    @PostMapping("/{itemId}/cart")
    public String addToCart(@Valid @ModelAttribute CartForm cartForm,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session) {


        Item findItem = itemRepository.findById(cartForm.getId());

        //재고 수량 초과로 장바구니 담을 시
        if (findItem.getQuantity() < cartForm.getQuantity()) {
            //bindingResult에 직접 에러 추가
            bindingResult.rejectValue("quantity","Exceed","상품 재고 수량을 초과할 수 없습니다.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("item",findItem);
            return "cart/cartForm";
        }

        Cart cart = getOrCreateCart(session);
        System.out.println("cartForm = " + cartForm);
        //아이템과 수량.
        cart.addItem(findItem, cartForm.getQuantity());
        return "redirect:/main";

    }

    //장바구니 전체 보여주는 뷰.
    @GetMapping("/cartAll")
    public String cartAll(Model model,
                          HttpSession session) {
        Cart cart = getOrCreateCart(session);

        model.addAttribute("cart", cart);
        return "cart/cart_view";

    }

    //장바구니 아이템 삭제
    @PostMapping("/cart/remove")
    public String cartItemRemove(@RequestParam Long itemId,
                                 HttpSession session) {
        Item findItem = itemRepository.findById(itemId);
        Cart cart = getOrCreateCart(session);
        cart.removeItem(findItem);
        return "redirect:/items/cartAll";
    }




    //메소드
    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("CART");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("CART", cart);
        }
        return cart;
    }
}
