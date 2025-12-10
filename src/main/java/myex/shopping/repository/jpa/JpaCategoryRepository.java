package myex.shopping.repository.jpa;

import myex.shopping.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

//Spring Data JPA 사용.
public interface JpaCategoryRepository extends JpaRepository<Category, Long> {
}
