package monk3.ecommerce.controller;

import java.io.IOException;
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

import monk3.ecommerce.exception.CustomException;
import monk3.ecommerce.model.Category;
import monk3.ecommerce.model.CategoryList;
import monk3.ecommerce.model.ProductList;
import monk3.ecommerce.repo.CategoryRepository;
import monk3.ecommerce.repo.ProductRepository;
import monk3.ecommerce.service.CatalogSyncService;

@RestController
@RequestMapping("/catalog")
public class CatalogSyncController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CatalogSyncController.class);

	@Autowired
	private CatalogSyncService catalogSyncService;
	@Autowired
	private CategoryRepository repository;

	@GetMapping("/categories")
	public ResponseEntity<List<Category>> getCategories() {
		List<Category> categories = repository.findAll();
		if (categories.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}

	@GetMapping("/sync")
	public ResponseEntity<CategoryList> syncCategories(@RequestParam int limit, @RequestParam int page) {
		try {
			CategoryList categoryList = catalogSyncService.syncCategories(limit, page);
			return new ResponseEntity<>(categoryList, HttpStatus.OK);
		} catch (IOException e) {
			LOGGER.error("Error occurred while syncing categories: ", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid input received while syncing categories: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/syncProducts")
	public ResponseEntity<ProductList> syncProducts(@RequestParam int limit, @RequestParam int page,
			@RequestParam String categoryID) {
		try {
			ProductList productList = catalogSyncService.syncProducts(limit, page, categoryID);
			return new ResponseEntity<>(productList, HttpStatus.OK);
		} catch (IOException e) {
			LOGGER.error("Error occurred while syncing products: ", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (CustomException e) {
			LOGGER.error("Custom exception occurred while syncing products: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid input received while syncing products: ", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
