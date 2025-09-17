package myex.shopping.controller;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.form.ItemAddForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository; //생성자 주입.
    private final ItemService itemService;

    //전체 아이템 조회
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "items/items";
    }

    //개별 아이템 상세 조회
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "items/item";

    }

    //아이템 추가
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new ItemAddForm()); //th:object 쓸려고 빈 객체 넣음.
        return "items/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute("item")ItemAddForm form,
                          RedirectAttributes redirectAttributes) throws IOException {

        //UploadFolder 에 있는 사진 업로드 시도 하면, "같은 경로 + 같은 파일명" 이라 같다고 판단해 move 불가능.(오류 발생. /UUID로 바꿀시, "같은 경로 + 다른 파일명" 이라 다른 파일이라 판단하고 업로드 가능.

        //"다른 경로 + 같은 파일명" : 덮어쓰기 해버림.
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
        return "redirect:/items/{itemId}";
    }

    //아이템 수정
    @GetMapping("/{itemId}/edit")
    public String editForm (@PathVariable("itemId") Long itemId,
                            Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);
        return "items/editForm"; //이건 뿌리는 뷰 (서버 html 위치)
    }

    @PostMapping("/{itemId}/edit") //이게 URL
    public String edit (@PathVariable Long itemId,
                        @ModelAttribute("item")ItemAddForm form) throws IOException {

        Item findItem = itemRepository.findById(itemId);
        Item item = itemService.imageEditSaveByUUID(form, findItem);
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        itemRepository.update(itemId, item);
//        itemRepository.update_exceptImgUrl(itemId, item);
        return "redirect:/items/{itemId}";
        //  {} 치환 순위.
        /*
          1. RedirectAttributes.addAttribute("itemId",...)
          2. @PathVariable, @RequestParma, 같은 요청
             메서드 파라미터 이름 Long itemId 랑 매칭됨.
        */
    }

    //아이템 삭제하기 넣기.
    @PostMapping("/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId) {
        itemRepository.deleteItem(itemId);
        return "redirect:/items";
    }

}
