package myex.shopping.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.Item;
import myex.shopping.dto.itemdto.ItemDto;
import myex.shopping.exception.ResourceNotFoundException;
import myex.shopping.form.ItemAddForm;
import myex.shopping.form.ItemEditForm;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.jpa.JpaItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ImageService imageService;
    private final EntityManager em;

    @Transactional(readOnly = false)
    public Item update(Long itemId, Item updateParam)
    {
        //영속성 컨텍스트가 관리. (Dirty Checking)
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));
        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
        item.setImageUrl(updateParam.getImageUrl());

        return item;
    }

    //id itemName, price, quantity (url 없음)
    @Transactional(readOnly = true)
    public List<ItemDto> findAllToDto() {
        return itemRepository.findAll()
                .stream()
                .map(ItemDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemDto findByIdToDto(Long id) {
        return itemRepository.findById(id)
                .map(ItemDto::new)
                .orElseThrow(() -> new ResourceNotFoundException("item not found"));
    }

    @Transactional(readOnly = false)
    public Long createItem(ItemAddForm form) throws IOException {
        String imageUrl = imageService.storeFile(form.getImageFile());
        Item item = new Item();
        //기본 필드 및 이미지 URL 저장.
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());
        item.setImageUrl(imageUrl);

        Item savedItem = itemRepository.save(item);
        return savedItem.getId();
    }

    @Transactional(readOnly = false)
    public Long editItemWithUUID(ItemEditForm form, Long itemId) throws IOException {
        String imageUrl = imageService.storeFile(form.getImageFile());
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("item not found"));
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());
        if (imageUrl != null) {
            item.setImageUrl(imageUrl);
        }
        itemRepository.save(item);
        em.flush();
        return item.getId();
    }

    public List<ItemDto> findSearchByNameDto(String keyword) {
        return itemRepository.searchByName(keyword)
                .stream()
                .map(ItemDto::new)
                .collect(Collectors.toList());

    }
}
