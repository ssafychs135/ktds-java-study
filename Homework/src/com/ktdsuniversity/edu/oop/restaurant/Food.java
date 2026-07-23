package com.ktdsuniversity.edu.oop.restaurant;

/**
 * 레스토랑에서 제공하는 음식.
 * 육고기, 채소, 과일, 생선이 있다.
 */
public class Food {

	/** 음식 이름 */
	private String name;

	/** 한 접시당 상승하는 포만감 */
	private int fullnessUp;

	/** 한 접시당 하락하는 취기 (채소, 과일만 해당) */
	private int drunkennessDown;

	public Food(String name, int fullnessUp, int drunkennessDown) {
		this.name = name;
		this.fullnessUp = fullnessUp;
		this.drunkennessDown = drunkennessDown;
	}

	public String getName() {
		return name;
	}

	public int getFullnessUp() {
		return fullnessUp;
	}

	public int getDrunkennessDown() {
		return drunkennessDown;
	}

}
