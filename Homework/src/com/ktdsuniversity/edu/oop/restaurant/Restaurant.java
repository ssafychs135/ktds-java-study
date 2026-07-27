package com.ktdsuniversity.edu.oop.restaurant;

/**
 * 웰빙 무한 레스토랑.
 * 음식과 주류를 손님에게 제공하며, 제공 가능 여부를 정책에 따라 판단한다.
 */
public class Restaurant {

	/** 이 나이 미만은 미성년자로, 주류를 제공하지 않는다. */
	private static final int ADULT_AGE = 19;

	/** 손님의 포만감이 이 수치를 초과하면 음식을 제공하지 않는다. */
	private static final int MAX_FULLNESS = 100;

	/** 손님의 취기가 이 수치를 초과하면 주류를 제공하지 않는다. */
	private static final int MAX_DRUNKENNESS = 60;

	/** 레스토랑 이름 */
	private String name;

	/** 제공하는 음식 */
	private Food[] foods;

	/** 제공하는 주류 */
	private Alcohol[] alcohols;

	public Restaurant(String name) {

		this.name = name;

		// 음식: 이름, 포만감 상승, 취기 하락
		this.foods = new Food[] {
				new Food("육고기", 25, 0),
				new Food("채소", 13, 5),
				new Food("과일", 13, 5),
				new Food("생선", 7, 0)
		};

		// 주류: 이름, 취기 상승, 포만감 상승, 포만감 하락
		this.alcohols = new Alcohol[] {
				new Alcohol("소주", 17, 0, 7),
				new Alcohol("맥주", 6, 8, 0),
				new Alcohol("위스키", 40, 0, 7),
				new Alcohol("꼬냑", 40, 0, 7)
		};
	}

	/**
	 * 손님에게 음식을 한 접시 제공한다.
	 * 포만감이 100%를 초과한 손님에게는 제공하지 않는다.
	 * @param customer 음식을 먹을 손님
	 * @param food 제공할 음식
	 * @return 제공 성공 여부
	 */
	public boolean serveFood(Customer customer, Food food) {

		if (customer.getFullness() > MAX_FULLNESS) {
			System.out.println(customer.getName() + "님은 배가 너무 불러 " + food.getName() + "을(를) 드실 수 없습니다."
					+ " (포만감: " + customer.getFullness() + "%)");
			return false;
		}

		customer.eat(food);

		System.out.println(customer.getName() + "님이 " + food.getName() + "을(를) 한 접시 드셨습니다."
				+ " (포만감: " + customer.getFullness() + "% / 취기: " + customer.getDrunkenness() + "%)");

		return true;
	}

	/**
	 * 손님에게 주류를 한 병 제공한다.
	 * 19세 미만의 미성년자에게는 제공하지 않으나, 부모님과 동반한 경우에는 제공한다.
	 * 취기가 60%를 초과한 손님에게도 제공하지 않는다.
	 * @param customer 주류를 마실 손님
	 * @param alcohol 제공할 주류
	 * @return 제공 성공 여부
	 */
	public boolean serveAlcohol(Customer customer, Alcohol alcohol) {

		if (customer.getAge() < ADULT_AGE && !customer.isWithParent()) {
			System.out.println(customer.getName() + "님은 미성년자이므로 주류를 제공해 드릴 수 없습니다."
					+ " (부모님과 동반 시 제공 가능)");
			return false;
		}

		if (customer.getDrunkenness() > MAX_DRUNKENNESS) {
			System.out.println(customer.getName() + "님은 취기가 올라 " + alcohol.getName() + "을(를) 드실 수 없습니다."
					+ " (취기: " + customer.getDrunkenness() + "%)");
			return false;
		}

		customer.drink(alcohol);

		System.out.println(customer.getName() + "님이 " + alcohol.getName() + "을(를) 한 병 드셨습니다."
				+ " (포만감: " + customer.getFullness() + "% / 취기: " + customer.getDrunkenness() + "%)");

		return true;
	}

	public void printMenu() {

		System.out.println("=== " + this.name + " 메뉴 ===");

		System.out.println("[음식]");
		for (int i = 0; i < this.foods.length; i++) {
			System.out.println("  " + this.foods[i].getName()
					+ " (포만감 +" + this.foods[i].getFullnessUp() + "%"
					+ (this.foods[i].getDrunkennessDown() > 0
							? ", 취기 -" + this.foods[i].getDrunkennessDown() + "%" : "") + ")");
		}

		System.out.println("[주류]");
		for (int i = 0; i < this.alcohols.length; i++) {
			System.out.println("  " + this.alcohols[i].getName()
					+ " (취기 +" + this.alcohols[i].getDrunkennessUp() + "%"
					+ (this.alcohols[i].getFullnessUp() > 0
							? ", 포만감 +" + this.alcohols[i].getFullnessUp() + "%" : "")
					+ (this.alcohols[i].getFullnessDown() > 0
							? ", 포만감 -" + this.alcohols[i].getFullnessDown() + "%" : "") + ")");
		}
	}

	public String getName() {
		return name;
	}

	public Food[] getFoods() {
		return foods;
	}

	public Alcohol[] getAlcohols() {
		return alcohols;
	}

}
