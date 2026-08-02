package mart;

public class NormalItem implements Item{
	
	private String productName;
	private int productPrice;

	public NormalItem(String productName, int productPrice) {
		this.productName = productName;
		this.productPrice = productPrice;
	}

}
