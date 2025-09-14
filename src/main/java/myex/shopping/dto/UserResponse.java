package myex.shopping.dto;

import java.util.List;

public record UserResponse(UserDto userDto, List<ItemDto> itemDto)  {
}
