package com.ktdsuniversity.edu.oop.restaurant;

/**
 * 레스토랑에서 제공하는 주류.
 * 소주, 맥주, 위스키, 꼬냑이 있다.
 */
public class Alcohol {

	/** 주류 이름 */
	private String name;

	/** 한 병당 상승하는 취기 */
	private int drunkennessUp;

	/** 한 병당 상승하는 포만감 (맥주만 해당) */
	private int fullnessUp;

	/** 한 병당 하락하는 포만감 (맥주를 제외한 주류) */
	private int fullnessDown;

	public Alcohol(String name, int drunkennessUp, int fullnessUp, int fullnessDown) {
		this.name = name;
		this.drunkennessUp = drunkennessUp;
		this.fullnessUp = fullnessUp;
		this.fullnessDown = fullnessDown;
	}

	public String getName() {
		return name;
	}

	public int getDrunkennessUp() {
		return drunkennessUp;
	}

	public int getFullnessUp() {
		return fullnessUp;
	}

	public int getFullnessDown() {
		return fullnessDown;
	}

}
