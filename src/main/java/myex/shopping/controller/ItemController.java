package myex.shopping.controller;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.form.ItemAddForm;
import myex.shopping.repository.ItemRepository;
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

        MultipartFile file = form.getImageFile();
        if (file != null && !file.isEmpty())
        {
            Item item = new Item();
            //서버에 저장할 경로
            String uploadDir = "C:/Users/kimsunjae/Desktop/NewFolder/Java_INTELLIJ/MyExample/UploadFolder/";
            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            //폴더 없으면 생성
            Files.createDirectories(filePath.getParent());

            //파일 저장
            file.transferTo(filePath.toFile());

            //브라우저에서 접근할 URL 생성
            item.setItemName(form.getItemName());
            item.setPrice(form.getPrice());
            item.setQuantity(form.getQuantity());
            item.setImageUrl("/img/"+fileName);

            Item savedItem = itemRepository.save(item);
            redirectAttributes.addAttribute("itemId", savedItem.getId());

            System.out.println(savedItem.getImageUrl());
            System.out.println(item.getImageUrl());
            System.out.println(item.getImageUrl());

        }


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
                        @ModelAttribute("item")Item item) throws IOException {
        MultipartFile file = item.getImageFile();
        if (file != null && !file.isEmpty())
        {
            //업로드 경로에 있는 파일 선택 테스트
            //서버에 저장할 경로
            String uploadDir = "C:/Users/kimsunjae/Desktop/NewFolder/Java_INTELLIJ/MyExample/UploadFolder/";
            String fileName = file.getOriginalFilename();

            //경로 + 파일명 이 둘 다 같으면 (Upload 폴더에 있는 사진 업로드 하면) 같은 파일로 판단해 move 불가능. -> 오류 발생. UUID로 이름 바꾸면 경로+파일명이 경로만 같아서 다른 파일이라고 판단하고 업로드 가능.
            //확장자
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String uniqueName = UUID.randomUUID().toString() +ext;

            Path filePath = Paths.get(uploadDir, uniqueName);

            //폴더 없으면 생성
            Files.createDirectories(filePath.getParent());

            //파일 저장
            file.transferTo(filePath.toFile());

            //브라우저에서 접근할 URL 생성
            item.setImageUrl("/img/"+uniqueName);
            System.out.println(item.getImageUrl());
            itemRepository.update(itemId, item);

        }

        itemRepository.update_exceptImgUrl(itemId, item);

        return "redirect:/items/{itemId}"; //@PathVariable 쓰면 itemId를 자동으로 모델에 넣어줘서 redirect"{itemId} 로 사용 가능. @PathVariable 안쓰면 오류.
    }


}
