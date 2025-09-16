package myex.shopping.controller.api;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.form.ItemAddForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ApiItemController {

    private final ItemRepository itemRepository; //생성자 주입.
    private final ItemService itemService;

    //전체 아이템 조회
    @GetMapping
    public ResponseEntity<List<Item>> items() {
        List<Item> items = itemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    //개별 아이템 상세 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<Item> item(@PathVariable long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item != null)
            return ResponseEntity.ok(item);
        else
            //바디 없이 상태 코드만 가진 ResponseEntity 반환 가능.
            return ResponseEntity.notFound().build();
    }
/*  Form 뷰를 호출 -> 프론트엔드.
    //아이템 추가
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new ItemAddForm()); //th:object 쓸려고 빈 객체 넣음.
        return "items/addForm";
    }

    */

    //아이템 추가 로직
    //AddForm, 프론트에서 imageFile, itemName, price, quantity 넘어옴.
    @PostMapping("/add")
    public ResponseEntity<Item> addItem(@ModelAttribute("item")ItemAddForm form,
                          RedirectAttributes redirectAttributes) throws IOException {

        Item item = itemService.ImageSave(form, new Item());
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());

        //맞는지 확인.
        System.out.println(savedItem.getImageUrl());
        System.out.println(item.getImageUrl());
        System.out.println("ItemController.addItem : postmapping");;
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    //아이템 수정 :상세 + 수정화면 공용
    //수정 화면이 필요하면 GET /items/{itemId} 에서 현재 상태를 가져와서 수정 폼에 채움.

    /*
    보통 이렇게 설계.
    GET /items -> 전체 목록 조회
    GET /items/{itemId} -> 단일 아이템 조회 
    PUT /items/{itemId} -> 수정 요청
     */
    
    //한개만 수정 : PutMapping
    @PutMapping("/{itemId}/edit") //이게 URL
    public ResponseEntity<Item> editItem (@PathVariable Long itemId,
                        @ModelAttribute("item")ItemAddForm form) throws IOException {


        Item item = itemService.imageEditSaveByUUID(form, new Item());
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        itemRepository.update(itemId, item);
        return ResponseEntity.ok(item);
    }


    @DeleteMapping("/{itemId}/delete")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {

        Item findItem = itemRepository.findById(itemId);
        if (findItem != null) {
            itemRepository.deleteItem(itemId);
            return ResponseEntity.noContent().build(); //204 No Content
        }
        else {
            return ResponseEntity.notFound().build(); //404 Not Found
        }
    }

}
