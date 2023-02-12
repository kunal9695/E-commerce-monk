package monk3.ecommerce.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import monk3.ecommerce.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

	List<Product> findByCategoryID(String categoryID, Pageable pageable);

}
