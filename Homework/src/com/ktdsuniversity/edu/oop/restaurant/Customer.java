package com.ktdsuniversity.edu.oop.restaurant;

/**
 * 레스토랑을 방문한 손님.
 * 자신의 포만감과 취기를 스스로 관리한다.
 */
public class Customer {

	/** 포만감이 이 수치를 초과하면 더 이상 먹을 수 없다. */
	private static final int MAX_FULLNESS = 100;

	/** 취기가 이 수치를 초과하면 더 이상 마실 수 없다. */
	private static final int MAX_DRUNKENNESS = 60;

	/** 손님 이름 */
	private String name;

	/** 손님 나이 */
	private int age;

	/** 부모님 동반 여부 */
	private boolean withParent;

	/** 포만감 (%) */
	private int fullness;

	/** 취기 (%) */
	private int drunkenness;

	public Customer(String name, int age, boolean withParent) {
		this.name = name;
		this.age = age;
		this.withParent = withParent;
	}

	/**
	 * 음식을 한 접시 먹는다.
	 * @param food 먹을 음식
	 * @return 먹었으면 true, 배가 불러 먹지 못했으면 false
	 */
	public boolean eat(Food food) {

		if (this.fullness > MAX_FULLNESS) {
			System.out.println(this.name + "님은 배가 너무 불러 " + food.getName() + "을(를) 드실 수 없습니다."
					+ " (포만감: " + this.fullness + "%)");
			return false;
		}

		this.fullness += food.getFullnessUp();

		// 채소, 과일은 취기를 떨어트린다.
		this.drunkenness -= food.getDrunkennessDown();

		if (this.drunkenness < 0) {
			this.drunkenness = 0;
		}

		System.out.println(this.name + "님이 " + food.getName() + "을(를) 한 접시 드셨습니다."
				+ " (포만감: " + this.fullness + "% / 취기: " + this.drunkenness + "%)");

		return true;
	}

	/**
	 * 주류를 한 병 마신다.
	 * @param alcohol 마실 주류
	 * @return 마셨으면 true, 취기가 높아 마시지 못했으면 false
	 */
	public boolean drink(Alcohol alcohol) {

		if (this.drunkenness > MAX_DRUNKENNESS) {
			System.out.println(this.name + "님은 취기가 올라 " + alcohol.getName() + "을(를) 드실 수 없습니다."
					+ " (취기: " + this.drunkenness + "%)");
			return false;
		}

		this.drunkenness += alcohol.getDrunkennessUp();

		// 맥주는 포만감을 올리고, 그 외 주류는 포만감을 떨어트린다.
		this.fullness += alcohol.getFullnessUp();
		this.fullness -= alcohol.getFullnessDown();

		if (this.fullness < 0) {
			this.fullness = 0;
		}

		System.out.println(this.name + "님이 " + alcohol.getName() + "을(를) 한 병 드셨습니다."
				+ " (포만감: " + this.fullness + "% / 취기: " + this.drunkenness + "%)");

		return true;
	}

	public void printStatus() {
		System.out.println("[" + this.name + " / " + this.age + "세 / 부모님 동반: "
				+ (this.withParent ? "O" : "X") + "] 포만감: " + this.fullness + "% / 취기: " + this.drunkenness + "%");
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public boolean isWithParent() {
		return withParent;
	}

	public int getFullness() {
		return fullness;
	}

	public int getDrunkenness() {
		return drunkenness;
	}

}
