package monk3.ecommerce.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;

@Builder
@Document(collection = "category")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

	private String id;
	private String name;
	

	@DBRef
	private List<Product> products;
	
	private int noOfProducts;

	public Category(int noOfProducts) {
		super();
		this.noOfProducts = noOfProducts;
	}

	public int getNoOfProducts() {
		return noOfProducts;
	}

	public void setNoOfProducts(int noOfProducts) {
		this.noOfProducts = noOfProducts;
	}

	public Category() {
	}

	public Category(String name, String id) {

		this.name = name;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	

}
