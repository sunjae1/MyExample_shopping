package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.dto.ItemDto;
import myex.shopping.form.ItemAddForm;
import myex.shopping.form.ItemEditForm;
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
import java.util.stream.Collectors;

/*
Delete return 은 성공했고, 반환값이 없으니까 No Content 204 응답.(삭제 완료)
Put, Patch는 클라이언트가 이미 성공값을 가지고 요청을 한거기 때문에, 서버는 "수정 완료" 만 보내면 된다.
(성공했고, 반환값이 없다) No Content 204 응답 사용 가능.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
@Tag(name = "Item", description = "상품 관련 API")
@Validated
public class ApiItemController {

    private final ItemRepository itemRepository; //생성자 주입.
    private final ItemService itemService;

    @Operation(
            summary = "전체 상품 조회",
            description = "모든 상품을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공")
            }
    )
    //전체 아이템 조회
    @GetMapping
    public ResponseEntity<List<ItemDto>> items() {
        List<ItemDto> items = itemRepository.findAll().stream()
                .map(ItemDto::new)
                .collect(Collectors.toList());


        return ResponseEntity.ok(items);
    }

    @Operation(
            summary = "개별 아이템 조회",
            description = "개별 아이템을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "개별 아이템 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "아이템이 없음")
            }
    )
    //개별 아이템 상세 조회
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> item(@PathVariable @Positive(message = "양수만 입력 가능합니다.") long itemId) {
        Optional<Item> findItemOpt = itemRepository.findById(itemId);

        if (findItemOpt.isPresent()) {
            ItemDto itemDto = new ItemDto(findItemOpt.get());
            return ResponseEntity.ok(itemDto);
        }
        else
            //바디 없이 상태 코드만 가진 ResponseEntity 반환 가능.
            return ResponseEntity.notFound().build();
    }
/*  Form 뷰를 호출 -> 프론트엔드.
    //아이템 추가
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new ItemAddForm()); //th:object 사용 위해 빈 객체 넣음.
        return "items/addForm";
    }
    */

    @Operation(
            summary = "상품 추가 등록",
            description = "상품 정보를 새로 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "등록 성공")
            }
    )
    //아이템 추가 로직
    //AddForm, 프론트에서 imageFile, itemName, price, quantity 넘어옴.
    //JSON + File => Postman - Body : form-data(multipart/form-data)
    //원래 Form은 url에 key=value로 전송.
    //multipart/form-data는 각 input 파트로 나눠서 전송.(텍스트+파일 가능)
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ItemDto> addItem(@Valid @ModelAttribute ItemAddForm form,
                          RedirectAttributes redirectAttributes) throws IOException {

        Long savedItemId = itemService.createItem(form);
        ItemDto itemDto = itemRepository.findById(savedItemId)
                .map(ItemDto::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템은 없습니다."));

        System.out.println("ItemController.addItem : postmapping");;
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
    }

    //아이템 수정 :상세 + 수정화면 공용
    //수정 화면이 필요하면 GET /items/{itemId} 에서 현재 상태를 가져와서 수정 폼에 채움.

    /*
    보통 이렇게 설계.
    GET /items -> 전체 목록 조회
    GET /items/{itemId} -> 단일 아이템 조회 
    PUT /items/{itemId} -> 수정 요청
     */

    @Operation(
            summary = "한 상품 수정",
            description = "상품 하나를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공")
            }
    )
    //한개만 수정 : PutMapping
    @PutMapping(value = "/{itemId}/edit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}) //이게 URL
    public ResponseEntity<ItemDto> editItem (@PathVariable @Positive(message = "양수만 입력 가능합니다") Long itemId,
                        @Valid @ModelAttribute ItemEditForm form) throws IOException {

        Long updateItemId = itemService.editItemWithUUID(form, itemId);
        ItemDto itemDto = itemRepository.findById(updateItemId)
                .map(ItemDto::new)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));


        return ResponseEntity.ok(itemDto);
    }


    @Operation(
            summary = "상품 삭제",
            description = "상품 정보를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "아이템 찾지 못함")
            }
    )
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
