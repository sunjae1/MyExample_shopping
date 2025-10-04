package myex.shopping.controller.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.form.ItemAddForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.memory.MemoryItemRepository;
import myex.shopping.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
Delete return 은 성공했고, 반환값이 없으니까 No Content 204 응답.
Put, Patch는 클라이언트가 이미 성공값을 가지고 요청을 한거기 때문에, 서버는 "수정 완료" 만 보내면 되기 때문에, (성공했고, 반환값이 없다) No Content 204 응답 사용 가능.
 */

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Validated
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
    public ResponseEntity<Item> item(@PathVariable @Positive(message = "양수만 입력 가능합니다.") long itemId) {
        Optional<Item> findItemOpt = itemRepository.findById(itemId);

        if (findItemOpt.isPresent()) {
            Item item = findItemOpt.get();
            return ResponseEntity.ok(item);
        }
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
    //JSON + File => Postman Body form-data(multipart/form-data)
    //원래 Form은 url에 key=value로 전송.
    //multipart/form-data는 각 input 파트로 나눠서 전송.(텍스트+파일 가능)
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Item> addItem(@Valid @ModelAttribute ItemAddForm form,
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
    @PutMapping(value = "/{itemId}/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) //이게 URL
    public ResponseEntity<Item> editItem (@PathVariable @Positive(message = "양수만 입력 가능합니다") Long itemId,
                        @Valid @ModelAttribute ItemAddForm form) throws IOException {

        Optional<Item> byId = itemRepository.findById(itemId);
        Item findItem = byId.get();
        Item item = itemService.imageEditSaveByUUID(form, findItem);
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        itemRepository.update(itemId, item);
        return ResponseEntity.ok(item);
    }


    @DeleteMapping("/{itemId}/delete")
    public ResponseEntity<?> deleteItem(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long itemId) {

        Optional<Item> findItemOpt = itemRepository.findById(itemId);
        if (findItemOpt.isPresent()) {

            Item findItem = findItemOpt.get();
            itemRepository.deleteItem(itemId);
            return ResponseEntity.noContent().build(); //204 No Content
        }
        else {
            return ResponseEntity.notFound().build(); //404 Not Found
        }
    }

}
