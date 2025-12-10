package myex.shopping.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myex.shopping.domain.Category;
import myex.shopping.dto.categorydto.CategoryCreateDTO;
import myex.shopping.dto.categorydto.CategoryEditDTO;
import myex.shopping.exception.ResourceNotFoundException;
import myex.shopping.repository.ItemRepository;
import myex.shopping.repository.jpa.JpaCategoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final JpaCategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    //등록
    public Category createCategory(@Valid CategoryCreateDTO createDTO) {
        Category category = new Category();
        category.setName(createDTO.getName());
        return categoryRepository.save(category);
    }
    //수정
    public Category updateCategory(Long id, CategoryEditDTO updateDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        //PATCH -> null :기존 값 유지, notNull -> 수정.
        if (updateDTO.getName() != null) {
            category.setName(updateDTO.getName());
        }

        //em.merge
        return categoryRepository.save(category);
    }
    //삭제
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category not found"));
        categoryRepository.delete(category);
    }



}
