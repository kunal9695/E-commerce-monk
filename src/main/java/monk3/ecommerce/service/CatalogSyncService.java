package monk3.ecommerce.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

	public void syncCategories() throws IOException {
		int threads = 20;
		for (int i = 1; ; i += threads) {
			final AtomicBoolean fetchedAllCategories =   new AtomicBoolean(false);
		    IntStream.range(i, i + threads).boxed().collect(Collectors.toList()).parallelStream() 
		            .forEach(pageNumber -> {
		            	CategoryList categoriesOnPageNumber = getCategoryResponse(pageNumber);
		            	if(categoriesOnPageNumber != null && categoriesOnPageNumber.getCategories() != null) {
		            		repository.saveAll(categoriesOnPageNumber.getCategories());
		            	}
		            	else {
		            		fetchedAllCategories.set(true);
		            	}
		            });
		    if(fetchedAllCategories.get()) break;

		}
	}
	
	private CategoryList getCategoryResponse(int pageNumber) {
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.set("x-api-key", "xbrs648d9aS7717");
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
			ResponseEntity<String> response = restTemplate.exchange(
					"https://stageapi.monkcommerce.app/task/categories?limit=100&page=" + pageNumber, HttpMethod.GET,
					entity, String.class);

			ObjectMapper mapper = new ObjectMapper();
			return  mapper.readValue(response.getBody(), CategoryList.class);
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
	}

	public ProductList syncProducts(String categoryID) throws IOException, CustomException {
		ProductList products = null;
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-api-key", "xmja813nd8as88po");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		int pageNumber = 1;
		boolean hasMoreProducts = true;

		try {
			while (hasMoreProducts) {
				ResponseEntity<String> response = restTemplate
						.exchange("https://stageapi.monkcommerce.app/task/products?limit=100" + "&categoryID="
								+ categoryID + "&page=" + pageNumber, HttpMethod.GET, entity, String.class);

				ObjectMapper mapper = new ObjectMapper();
				ProductList productList = mapper.readValue(response.getBody(), ProductList.class);

				if (productList != null && productList.getProducts() != null && !productList.getProducts().isEmpty()) {
					if (products == null) {
						products = productList;
					} else {
						products.getProducts().addAll(productList.getProducts());
					}
					pageNumber++;
				} else {
					hasMoreProducts = false;
				}
			}

			if (products != null && products.getProducts() != null && !products.getProducts().isEmpty()) {
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
				logger.error(
						"Products are null or empty in the response while syncing products with the given categoryID.");
				throw new CustomException(
						"Products are null or empty in the response while syncing products with the given categoryID.");
			}
		} catch (RestClientException e) {
			logger.error("Error while syncing products: ", e);
			throw new CustomException("Error while syncing products with the given categoryID.");
		} catch (IOException e) {
			logger.error("Error while syncing products: ", e);
			throw new CustomException("Error while parsing the response while syncing products.");
		}

		return products;
	}
}
