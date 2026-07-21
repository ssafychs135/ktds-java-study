package homework;

/**
 * 단일 상품을 판매하는 판매자. - price : 상품 단가(원) - stock : 재고 개수 - revenue : 매출액(원) 판매자마다
 * 단가/재고/매출액이 다를 수 있다.
 */
public class Seller {
	private int price; // 상품 단가(원)
	private int stock; // 재고 개수
	private int revenue; // 매출액(원)

	// 생성자 (1) : 매출액은 0에서 시작
	public Seller(int price, int stock) {
		this(price, stock, 0); // 아래 생성자로 위임 (생성자 체이닝)
	}

	// 생성자 (2) : 초기 매출액까지 지정 (생성자 오버로딩)
	public Seller(int price, int stock, int revenue) {
		this.price = price;
		this.stock = stock;
		this.revenue = revenue;
	}

	/**
	 * 요청 수량을 재고 한도 내에서만 판매한다. 재고가 소진됐으면 0개를 판매한다(더 이상 판매하지 않음).
	 * 
	 * @return 실제로 판매한 개수
	 */
	public int sell(int quantity) {
		int soldCount = Math.min(quantity, stock); // 재고보다 많이 요청하면 재고만큼만
		if (soldCount <= 0) {
			return 0; // 재고 없음 또는 잘못된 요청
		}
		stock -= soldCount; // 재고 차감
		revenue += price * soldCount; // 매출액 증가 (환불 없음)
		return soldCount;
	}

	public int getPrice() {
		return price;
	}

	public int getStock() {
		return stock;
	}

	public int getRevenue() {
		return revenue;
	}
}