package myex.shopping.controller.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.Cart;
import myex.shopping.domain.Item;
import myex.shopping.domain.User;
import myex.shopping.dto.cartdto.CartDto;
import myex.shopping.dto.itemdto.ItemDto;
import myex.shopping.dto.userdto.UserDto;
import myex.shopping.exception.ResourceNotFoundException;
import myex.shopping.form.CartForm;
import myex.shopping.repository.CartRepository;
import myex.shopping.repository.ItemRepository;
import myex.shopping.service.CartService;
import myex.shopping.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class CartController {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final ItemService itemService;
    private final CartService cartService;

    //한 상품에 대한 주문 페이지를 보여주고, 장바구니 담기 클릭시 장바구니에 저장.
    // /처음 쇼핑몰 홈페이지 --> 장바구니 담기 버튼.
    //@PathVariable Long itemid 로 item 꺼내서 뷰에 item 전달.
    @GetMapping("/{itemId}/cart")
    public String viewCart(@PathVariable Long itemId,
                           @ModelAttribute CartForm cartForm,
                           HttpSession session,
                           Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            UserDto userDto = new UserDto(loginUser);
            model.addAttribute("user",userDto);
        }
        ItemDto item = itemService.findByIdToDto(itemId);
        model.addAttribute("item", item);
        return "cart/cartForm";
    }

    //한 상품에 대한 주문 페이지에서 정보가 넘어오면 장바구니에 저장.
    //cartForm 에서 id - item.id 매핑됨.
    // id, price, quantity --> CartForm : id, quantity 매핑.
    @PostMapping("/{itemId}/cart")
    public String addToCart(@Valid @ModelAttribute CartForm cartForm,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session) {
        log.info("cart 상품 추가 컨트롤러 진입");
        log.info("cartForm 정보 : {}", cartForm);

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            UserDto userDto = new UserDto(loginUser);
            model.addAttribute("user",userDto);
        }
        Item item = itemRepository.findById(cartForm.getId())
                .orElseThrow(() -> new ResourceNotFoundException("item not found"));
        if (bindingResult.hasErrors()) {
            log.info("검증 실패 : {}",bindingResult);
            model.addAttribute("item",item);
            return "cart/cartForm";
        }
        //재고 수량 초과로 장바구니 담을 시
        if (item.getQuantity() < cartForm.getQuantity()) {
            log.info("장바구니 담기 : 재고 수량 초과 오류");
            //bindingResult에 직접 에러 추가
            bindingResult.rejectValue("quantity","Exceed","상품 재고 수량을 초과할 수 없습니다.");
            model.addAttribute("item",item);
            return "cart/cartForm";
        }
        //사용자 장바구니 가져오거나 없으면 생성.
//        User loginUser = (User) session.getAttribute("loginUser");
        Cart cart = cartService.findOrCreateCartForUser(loginUser);
        //아이템과 수량 추가.
        boolean result = cart.addItem(item, cartForm.getQuantity());
        if (result == false)
        {
            log.info("장바구니 담기 : 전체 장바구니 수량이 상품 총 수량을 넘었습니다.");
            bindingResult.rejectValue("quantity", "TotalExceed","전체 장바구니 수량이 상품 총 수량을 넘었습니다.");
        }
        if (bindingResult.hasErrors()) {
            log.info("검증 실패 : {}", bindingResult);
            model.addAttribute("item",item);
            return "cart/cartForm";
        }
        //Cart 존재하면, merge
        if (cart.getId() != null) {
            log.info("장바구니 담기 : em.merge");
            cartService.update(cart);
        }
        log.info("cart.getId() : {}", cart.getId());
        return "redirect:/";
    }
    //장바구니 전체 보여주는 뷰.
    @GetMapping("/cartAll")
    public String cartAll(Model model,
                          HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");

        CartDto cart = cartService.findByUserByDto(loginUser);
        model.addAttribute("cart", cart);
        return "cart/cart_view";
    }
    //장바구니 아이템 삭제
    @PostMapping("/cart/remove")
    public String cartItemRemove(@RequestParam Long itemId,
                                 HttpSession session) {
        //Hashmap 에서 get, remove 둘 다 없는 값이면 null 반환이라 그렇게 -1 검증 X.
        //Service 에서 유효 값 검증
        cartService.deleteItem(itemId, session);
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
