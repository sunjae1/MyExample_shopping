package myex.shopping.service;

import myex.shopping.domain.Item;
import myex.shopping.dto.itemdto.ItemDto;
import myex.shopping.form.ItemAddForm;
import myex.shopping.form.ItemEditForm;
import myex.shopping.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("아이템 전체 조회를 DTO로 변환하여 반환한다")
    void findAllToDto() {
        // given
        Item item1 = new Item("itemA", 10000, 10, "/img/itemA.jpg");
        Item item2 = new Item("itemB", 20000, 20, "/img/itemB.jpg");
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        // when
        List<ItemDto> result = itemService.findAllToDto();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getItemName()).isEqualTo("itemA");
        assertThat(result.get(1).getItemName()).isEqualTo("itemB");
    }

    @Test
    @DisplayName("아이템을 생성하고 저장한다")
    void createItem() throws IOException {
        // given
        ItemAddForm form = new ItemAddForm();
        form.setItemName("New Item");
        form.setPrice(15000);
        form.setQuantity(30);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "test image".getBytes());
        form.setImageFile(imageFile);

        Item savedItem = new Item("New Item", 15000, 30);
        savedItem.setId(1L);
        savedItem.setImageUrl("/img/test.jpg");

        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        // when
        Long itemId = itemService.createItem(form);

        // then
        assertThat(itemId).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("아이템 정보를 수정한다")
    void update() {
        // given
        Long itemId = 1L;
        Item existingItem = new Item("Old Name", 100, 10);
        Item updateParam = new Item("New Name", 200, 20);
        updateParam.setImageUrl("/img/new.jpg");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // when
        Item updatedItem = itemService.update(itemId, updateParam);

        // then
        assertThat(updatedItem.getItemName()).isEqualTo("New Name");
        assertThat(updatedItem.getPrice()).isEqualTo(200);
        assertThat(updatedItem.getQuantity()).isEqualTo(20);
        assertThat(updatedItem.getImageUrl()).isEqualTo("/img/new.jpg");
    }

    @Test
    @DisplayName("아이템 정보를 UUID와 함께 수정한다")
    void editItemWithUUID() throws IOException {
        // given
        Long itemId = 1L;
        ItemEditForm form = new ItemEditForm();
        form.setItemName("Edited Item");
        form.setPrice(25000);
        form.setQuantity(50);
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "edit.png", "image/png", "edited image".getBytes());
        form.setImageFile(imageFile);

        Item existingItem = new Item("Original Item", 10000, 10);
        existingItem.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // when
        Long editedItemId = itemService.editItemWithUUID(form, itemId);

        // then
        assertThat(editedItemId).isEqualTo(itemId);
        assertThat(existingItem.getItemName()).isEqualTo("Edited Item");
        assertThat(existingItem.getPrice()).isEqualTo(25000);
        assertThat(existingItem.getQuantity()).isEqualTo(50);
        assertThat(existingItem.getImageUrl()).contains(".png");
    }
}
