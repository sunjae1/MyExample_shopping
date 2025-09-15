package myex.shopping.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemAddForm {

    private String itemName;
    private int price;
    private int quantity;
    private MultipartFile imageFile;

}
