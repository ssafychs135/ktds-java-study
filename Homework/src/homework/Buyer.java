package homework;

/**
 * 단일 상품을 구매하는 구매자. 
 * - cash : 보유 현금(원) 
 * - cart : 장바구니에 담긴 상품 개수 구매자마다 현금/장바구니 개수가 다를 수 있다.
 */
public class Buyer {
	private int cash; // 보유 현금(원)
	private int cart; // 장바구니 개수

	// 생성자 (1) : 장바구니는 0에서 시작
	public Buyer(int cash) {
		this(cash, 0); // 아래 생성자로 위임
	}

	// 생성자 (2) : 초기 장바구니 개수까지 지정 (생성자 오버로딩)
	public Buyer(int cash, int cart) {
		this.cash = cash;
		this.cart = cart;
	}

	/**
	 * 판매자로부터 quantity개 구매를 시도한다.
	 *
	 * 1) 재고 한도 : 재고보다 많이 요청하면 재고만큼만 구매 (부분 구매 허용) 
	 * 2) 현금 한도 : 구매 총액이 현금을 초과하면 거래 실패
	 * → 양쪽 모두 아무 변화 없음
	 *
	 * @return 실제로 구매한 개수
	 */
	public int buy(Seller seller, int quantity) {
		if (quantity <= 0) {
			return 0;
		}

		// 1) 재고 한도 내에서 실제 구매 가능한 개수 결정
		int available = Math.min(quantity, seller.getStock());
		if (available <= 0) {
			return 0; // 재고 없음
		}

		// 2) 현금 확인 — 부족하면 판매/구매 어느 쪽도 손대지 않고 실패
		int cost = available * seller.getPrice();
		if (cost > cash) {
			return 0; // 거래 미성사: 구매자·판매자 모두 변화 없음
		}

		// 3) 거래 성사: 판매자가 실제 판매 처리 → 그 결과로 현금/장바구니 갱신
		int bought = seller.sell(available);
		cash -= bought * seller.getPrice();
		cart += bought;
		return bought;
	}

	public int getCash() {
		return cash;
	}

	public int getCart() {
		return cart;
	}
}
