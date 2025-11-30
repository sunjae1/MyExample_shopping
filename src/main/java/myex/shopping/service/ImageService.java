package myex.shopping.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {

    private String uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "img").toString();


    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        uploadDir = "../UploadFolder/";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

        System.out.println("uploadDir = " + uploadDir);
        // Create upload directory if it doesn't exist
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
//        Path filePath = Paths.get(uploadDir, storeFileName);
        Path filePath = uploadPath.resolve(storeFileName);

        multipartFile.transferTo(filePath);

        return "/img/" + storeFileName;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
