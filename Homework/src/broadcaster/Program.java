package broadcaster;

import java.time.LocalTime;

/**
 * 프로그램 한 편.
 *
 * 이름과 방영 구간(시작 ~ 종료)만 가지며, 자기가 방영 중인지 스스로 판단한다.
 * 다른 프로그램이 무엇인지는 알지 못한다.
 */
public class Program {

	private final String name; // 프로그램명
	private final LocalTime startTime; // 방영 시작 시각
	private final LocalTime endTime; // 방영 종료 시각

	/**
	 * @param name      프로그램명
	 * @param startTime "06:00" 형태의 시작 시각
	 * @param endTime   "09:30" 형태의 종료 시각
	 */
	public Program(String name, String startTime, String endTime) {
		this.name = name;
		this.startTime = LocalTime.parse(startTime);
		this.endTime = LocalTime.parse(endTime);
	}

	/**
	 * 주어진 시각이 이 프로그램의 방영 구간(양 끝 포함)에 들어오는지 판단한다.
	 */
	public boolean isOnAir(LocalTime time) {
		// LocalTime에는 isBeforeOrEqual이 없으므로 부정으로 뒤집는다.
		// (startTime <= time && time <= endTime 과 같은 뜻)
		return !time.isBefore(this.startTime) && !time.isAfter(this.endTime);
	}

	public String getName() {
		return this.name;
	}

}
