package monk3.ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import monk3.ecommerce.exception.InvalidInputException;
import monk3.ecommerce.exception.ResourceNotFoundException;
import monk3.ecommerce.model.Category;
import monk3.ecommerce.model.Product;
import monk3.ecommerce.service.CatalogService;

@RestController
@RequestMapping("/shop")
public class CatalogController {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogController.class);

	@Autowired
	CatalogService catalogService;

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getCategories(@RequestParam(defaultValue = "10") int limit,
			@RequestParam(defaultValue = "0") int page) {
		try {
			List<Category> categories = catalogService.getCategories(limit, page);
			return new ResponseEntity<>(categories, HttpStatus.OK);
		} catch (InvalidInputException e) {
			LOG.error("Error while fetching categories: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			LOG.error("An error occurred while retrieving categories", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam String categoryID,
			@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int page) {
		try {
			List<Product> products = catalogService.getProductsByCategory(categoryID, limit, page);
			return new ResponseEntity<>(products, HttpStatus.OK);
		} catch (InvalidInputException e) {
			LOG.error("Error while fetching products: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ResourceNotFoundException e) {
			LOG.error("Error while fetching products: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			LOG.error("An error occurred while retrieving products", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}