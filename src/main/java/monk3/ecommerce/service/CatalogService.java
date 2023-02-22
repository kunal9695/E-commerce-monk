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

import monk3.ecommerce.exception.CustomException;
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
	
	@Autowired
	private CatalogSyncService catalogSyncService;

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
	        
	        // Check if there are any products for the given category in the database
	        Pageable pageable = PageRequest.of(page, limit, Sort.by("customerReviewCount").descending());
	        List<Product> productsInDB = productRepository.findByCategoryID(categoryID, pageable);
	        if (productsInDB.isEmpty()) {
	            // No products in the database for the given category, so call syncProducts
	            catalogSyncService.syncProducts(categoryID);
	            productsInDB = productRepository.findByCategoryID(categoryID, pageable);
	            // Reload the category to get the updated noOfProducts value
	            Optional<Category> category = categoryRepository.findById(categoryID);
	            if (!category.isPresent()) {
	                throw new CustomException("Category not found after sync.");
	            }
	        } else {
	            System.out.println("Products for category " + categoryID + " found in database.");
	        }
	        return productsInDB;
	        
	    } catch (Exception e) {
	        logger.error("An error occurred while retrieving products", e);
	        throw new Exception("An error occurred while retrieving products", e);
	    }
	} 
}
