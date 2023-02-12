package monk3.ecommerce.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Document(collection = "product")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

	private String categoryID;

	private String sku;

	private String name;

	private double salePrice;

	@DBRef
	private List<Image> images;

	private String description;

	private int customerReviewCount;

	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Product(String categoryID, String sku, String name, double salePrice, List<Image> images, String description,
			int customerReviewCount) {
		super();
		this.categoryID = categoryID;
		this.sku = sku;
		this.name = name;
		this.salePrice = salePrice;
		this.images = images;
		this.description = description;
		this.customerReviewCount = customerReviewCount;
	}

	public String getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCustomerReviewCount() {
		return customerReviewCount;
	}

	public void setCustomerReviewCount(int customerReviewCount) {
		this.customerReviewCount = customerReviewCount;
	}

}
