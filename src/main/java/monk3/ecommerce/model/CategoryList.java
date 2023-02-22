package monk3.ecommerce.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;

@Builder
public class CategoryList {

	private int page;

	@DBRef
	private List<Category> categories;

	public CategoryList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CategoryList(int page, List<Category> categories) {
		super();
		this.page = page;
		this.categories = categories;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
