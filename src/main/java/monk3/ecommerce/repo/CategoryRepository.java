package monk3.ecommerce.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import monk3.ecommerce.model.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category, Integer> {
	List<Category> findAllByOrderByProductsDesc(Pageable pageable);

	Optional<Category> findById(String categoryID);
}
