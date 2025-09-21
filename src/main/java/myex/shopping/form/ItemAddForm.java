package myex.shopping.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ItemAddForm {

    @NotBlank(message = "아이템 이름을 입력하세요.")
    private String itemName;

    //int는 기본형이라 Null 불가, 무조건 값이 들어가고, 사용자가 입력 안하면 0 이 무조건 들어가서 검증이 안됨.
    @NotNull(message = "가격을 입력해주세요")
    @Range(min = 10, max = 999999999, message = "최소 10원, 최대 9억99,999,999 까지 입력 가능합니다.")
    private Integer price;
    @NotNull(message = "수량을 입력해주세요")
    @Max(value = 9999, message = "최대 수량은 9999개 입니다.")
    private Integer quantity;
    private MultipartFile imageFile;

}
