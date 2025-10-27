package myex.shopping.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 및 상품 통합 DTO")
public record UserResponse(UserDto userDto, List<ItemDto> itemDto)  {
}
