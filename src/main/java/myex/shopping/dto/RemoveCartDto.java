package myex.shopping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveCartDto {

    @NotNull(message = "Null 허용 불가.")
    @Positive(message = "양수만 입력가능합니다.")
    private Long itemId;
}
