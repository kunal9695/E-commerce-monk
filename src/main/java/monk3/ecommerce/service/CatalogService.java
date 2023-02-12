package monk3.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import monk3.ecommerce.exception.InvalidInputException;
import monk3.ecommerce.exception.ResourceNotFoundException;
import monk3.ecommerce.model.Category;
import monk3.ecommerce.model.Image;
import monk3.ecommerce.model.Product;
import monk3.ecommerce.repo.CategoryRepository;
import monk3.ecommerce.repo.ProductRepository;

@Service
public class CatalogService {

	private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	public List<Category> getCategories(int limit, int page) throws Exception {
		try {
			if (limit < 1) {
				throw new InvalidInputException("Limit should be at least 1.");
			}
			if (page < 0) {
				throw new InvalidInputException("Page number should be at least 0.");
			}
			Pageable pageable = PageRequest.of(page, limit, Sort.by("noOfProducts").descending());
			return categoryRepository.findAllByOrderByProductsDesc(pageable);
		} catch (Exception e) {
			logger.error("An error occurred while retrieving categories", e);
			throw new Exception("An error occurred while retrieving categories", e);
		}
	}

	public List<Product> getProductsByCategory(String categoryID, int limit, int page) throws Exception {
		try {
			if (limit < 1) {
				throw new InvalidInputException("Limit should be at least 1.");
			}
			if (page < 0) {
				throw new InvalidInputException("Page number should be at least 0.");
			}
			if (categoryID == null || categoryID.isEmpty()) {
				throw new InvalidInputException("Category ID is required.");
			}
			Optional<Category> category = categoryRepository.findById(categoryID);
			if (!category.isPresent()) {
				throw new ResourceNotFoundException("Category not found with id: " + categoryID);
			}
			Pageable pageable = PageRequest.of(page, limit, Sort.by("customerReviewCount").descending());
			List<Product> products = productRepository.findByCategoryID(categoryID, pageable);
			for (Product product : products) {
				List<Image> images = new ArrayList<>();
				 if (!product.getImages().isEmpty()) {
				        Image image = product.getImages().get(0);
				        Image newImage = new Image();
				        newImage.setId(image.get("$id", String.class));
				        newImage.setRef(image.get("$ref", String.class));
				        images.add(newImage);      
				    }
				product.setImages(images);
			}
			return productRepository.findByCategoryID(categoryID, pageable);
		} catch (Exception e) {
			logger.error("An error occurred while retrieving products", e);
			throw new Exception("An error occurred while retrieving products", e);
		}
	}

}
