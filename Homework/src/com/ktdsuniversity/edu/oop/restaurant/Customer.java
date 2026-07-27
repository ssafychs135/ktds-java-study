package com.ktdsuniversity.edu.oop.restaurant;

/**
 * 레스토랑을 방문한 손님.
 * 자신의 포만감과 취기를 스스로 관리한다.
 * 먹을 수 있는지, 마실 수 있는지는 레스토랑이 판단한다.
 */
public class Customer {

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
	 */
	public void eat(Food food) {

		this.fullness += food.getFullnessUp();

		// 채소, 과일은 취기를 떨어트린다.
		this.drunkenness -= food.getDrunkennessDown();

		if (this.drunkenness < 0) {
			this.drunkenness = 0;
		}
	}

	/**
	 * 주류를 한 병 마신다.
	 * @param alcohol 마실 주류
	 */
	public void drink(Alcohol alcohol) {

		this.drunkenness += alcohol.getDrunkennessUp();

		// 맥주는 포만감을 올리고, 그 외 주류는 포만감을 떨어트린다.
		this.fullness += alcohol.getFullnessUp();
		this.fullness -= alcohol.getFullnessDown();

		if (this.fullness < 0) {
			this.fullness = 0;
		}
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
