package myex.shopping.service;

import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Item;
import myex.shopping.form.ItemAddForm;
import myex.shopping.repository.memory.MemoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final MemoryItemRepository memoryItemRepository;

    public Item ImageSave(ItemAddForm form, Item item) throws IOException {
        MultipartFile file = form.getImageFile();
        if (file !=null && !file.isEmpty())
        {
            //업로드
            //서버에 저장할 경로
            String uploadDir = "../UploadFolder";

            //프로젝트 폴더 루트 기준 -> 절대경로로 바꿈.
            //상대 경로는 move 명령어 쓸 때 "현재 실행 위치" 기준.
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            //상대경로를 절대경로로 바꿔서 저장시켜야함. (상대경로로 저장 명령어 transferTo 하면, Tomcat 임시 폴더 : C:\Users\kimsunjae\AppData\Local\Temp\tomcat.8080.xxx\work\Tomcat\localhost\ROOT 이런식으로 됨.
            //톰캣 임시폴더에 UploadFolder 이런거 없거나 접근 권한이 없기 때문에 FileNotFoundException



            String fileName = file.getOriginalFilename(); //파일의 원래 이름, 확장자도 포함. 1.png

            Path filePath = uploadPath.resolve(fileName); //Path 뒤에다 붙여서 Path 만듬.
            //폴더 경로 + 파일 이름 => 파일 경로.


            //폴더 없으면 생성
            Files.createDirectories(filePath.getParent());

            //파일 저장(move 같은 운영체제 명령어 사용) (상대경로 그대로 쓰면 톰캣 임시 폴더에서 뒤지며, 접근 권한 없어서 Exception 터짐.)
            file.transferTo(filePath.toFile());

            //접근
            //브라우저에서 접근할 URL 생성
            item.setImageUrl("/img/"+fileName); // /img/1.jpg

        }
        return item;
    }


    //UUID로 이미지 업로드 - 업로드 폴더에서 가져와도 이름이 달라서 "같은 경로 + 다른 이름" 이라 다르게 취급.
    public Item imageEditSaveByUUID(ItemAddForm form, Item item) throws IOException {
        MultipartFile file = form.getImageFile();
        if (file !=null && !file.isEmpty())
        {
            String uploadDir = "../UploadFolder/";

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

            String fileName = file.getOriginalFilename(); // 1.jpg
            //같은경로 + 같은 파일명 이 둘 다 같으면 (Upload 폴더에 있는 사진 업로드 하면) 같은 파일로 판단해 move 불가능. -> 오류 발생. UUID로 이름 바꾸면 경로+파일명이 경로만 같아서 다른 파일이라고 판단하고 업로드 가능.
            //다른경로 +같은 파일명 : 덮어쓰기 해버림.

            //확장자
            String ext = fileName.substring(fileName.lastIndexOf(".")); //.부터 끝-1 까지 : .jpg
            String uniqueName = UUID.randomUUID().toString() + ext;
//            Path filePath = Paths.get(uploadDir, uniqueName); // ../UploadFolder/111www333.jpg

            Path filePath = uploadPath.resolve(uniqueName);

            //폴더 없으면 생성
            Files.createDirectories(filePath.getParent());

            //파일 저장
            file.transferTo(filePath.toFile());

            //브라우저에서 접근할 URL 생성
            item.setImageUrl("/img/"+uniqueName);
            //확인용
            System.out.println(item.getImageUrl());
        }
        return item;
    }
}
