package mart;

public class FreshItem extends NormalItem {

	private String expireDate;
	private int celsius;

	public FreshItem(String productName, int productPrice, String expireDate, int celsius) {
		super(productName, productPrice);

		this.expireDate = expireDate;
		this.celsius = celsius;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public int getCelsius() {
		return celsius;
	}

}