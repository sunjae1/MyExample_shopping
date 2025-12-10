package myex.shopping.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myex.shopping.domain.Category;
import myex.shopping.dto.categorydto.CategoryCreateDTO;
import myex.shopping.dto.categorydto.CategoryDTO;
import myex.shopping.dto.categorydto.CategoryEditDTO;
import myex.shopping.repository.jpa.JpaCategoryRepository;
import myex.shopping.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
@Tag(name = "Category", description = "카테고리 관련 API")
@Validated
public class ApiCategoryController {

    private final JpaCategoryRepository categoryRepository;
    private final CategoryService categoryService;

    //전체조회
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryRepository.findAll()
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }
    //단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable @Positive(message = "양수만 입력 가능합니다.") Long id) {
        return categoryRepository.findById(id)
                .map(CategoryDTO::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    //등록
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryCreateDTO createDTO) {
        CategoryDTO categoryDTO = new CategoryDTO(categoryService.createCategory(createDTO));
        return ResponseEntity.status(201).body(categoryDTO);
    }
    //수정
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable @Positive(message = "양수만 가능합니다.") Long id,
                                                      @RequestBody CategoryEditDTO updateDTO) {
        Category category = categoryService.updateCategory(id, updateDTO);
        return ResponseEntity.ok(new CategoryDTO(category));
    }
    //삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
