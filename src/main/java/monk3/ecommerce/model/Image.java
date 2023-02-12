package monk3.ecommerce.model;

import org.springframework.data.annotation.Id;

public class Image {

	@Id
	private String href;

	public Image() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Image(String href) {
		super();
		this.href = href;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Object get(String string, Class<String> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setId(Object object) {
		// TODO Auto-generated method stub
		
	}

	public void setRef(Object object) {
		// TODO Auto-generated method stub
		
	}

}
