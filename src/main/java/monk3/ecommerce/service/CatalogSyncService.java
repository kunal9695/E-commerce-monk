package monk3.ecommerce.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import monk3.ecommerce.exception.CustomException;
import monk3.ecommerce.model.Category;
import monk3.ecommerce.model.CategoryList;
import monk3.ecommerce.model.Product;
import monk3.ecommerce.model.ProductList;
import monk3.ecommerce.repo.CategoryRepository;
import monk3.ecommerce.repo.ProductRepository;

@Service
public class CatalogSyncService {

	@Autowired
	private CategoryRepository repository;

	@Autowired
	private ProductRepository productRepository;

	private static final Logger logger = LoggerFactory.getLogger(CatalogSyncService.class);

	public CategoryList syncCategories(int limit, int page) throws IOException {

		CategoryList categories = null;

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-api-key", "xbrs648d9aS7717");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					"https://stageapi.monkcommerce.app/task/categories?limit=" + limit + "&page=" + page,
					HttpMethod.GET, entity, String.class);

			ObjectMapper mapper = new ObjectMapper();
			categories = mapper.readValue(response.getBody(), CategoryList.class);

			for (Category category : categories.getCategories()) {
				repository.save(category);
			}

		} catch (HttpClientErrorException e) {
			logger.error("An error occurred while trying to fetch categories. Error code: {}", e.getStatusCode(), e);
			throw e;
		} catch (IOException e) {
			logger.error("An error occurred while trying to parse categories. Error: {}", e.getMessage(), e);
			throw new RuntimeException("An error occurred while trying to parse categories.", e);
		} catch (Exception e) {
			logger.error("An unknown error occurred while trying to fetch categories. Error: {}", e.getMessage(), e);
			throw new RuntimeException("An unknown error occurred while trying to fetch categories.", e);
		}
		return categories;
	}

	public ProductList syncProducts(int limit, int page, String categoryID) throws IOException, CustomException {
		ProductList products = null;
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-api-key", "xmja813nd8as88po");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		try {
			ResponseEntity<String> response = restTemplate
					.exchange("https://stageapi.monkcommerce.app/task/products?limit=" + limit + "&page=" + page
							+ "&categoryID=" + categoryID, HttpMethod.GET, entity, String.class);

			ObjectMapper mapper = new ObjectMapper();
			products = mapper.readValue(response.getBody(), ProductList.class);

			if (products != null && products.getProducts() != null) {
				// Map to store the count of unique products for the given categoryID
				Map<String, Integer> productCountMap = new HashMap<>();
				for (Product product : products.getProducts()) {
					product.setCategoryID(categoryID);
					String sku = product.getSku();
					// Check if the product with the given SKU has already been stored
					if (!productCountMap.containsKey(sku)) {
						// If not, save the product and add the SKU to the map
						productRepository.save(product);
						productCountMap.put(sku, 1);
					}
				}
				// Store the count of unique products in the database
				Category category = repository.findById(categoryID).orElse(null);
				if (category != null) {
					category.setNoOfProducts(productCountMap.size());
					repository.save(category);
				} else {
					logger.error("Category with the given categoryID not found.");
					throw new CustomException("Category with the given categoryID not found.");
				}
			} else {
				logger.error("Products are null in the response while sync products with the given categoryID.");
				throw new CustomException("Products are null in the response while sync products with the given categoryID.");
			}
			
		} catch (RestClientException e) {
			logger.error("Error while syncProducts: ", e);
			throw new CustomException("Error while sync products with the given categoryID.");
		} catch (IOException e) {
			logger.error("Error while syncProducts: ", e);
			throw new CustomException("Error while parsing the response while sync products.");
		}
		return products;
	}

}
