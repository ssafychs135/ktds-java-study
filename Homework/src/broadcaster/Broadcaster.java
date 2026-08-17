package broadcaster;

import java.time.LocalTime;

/**
 * 방송사 하나.
 *
 * 방송사는 프로그램"이" 아니라 프로그램"을 가진다". (Has-A)
 * 그래서 Program을 상속하지 않고 Program[]을 필드로 들고 있다.
 */
public class Broadcaster {

	private final String name; // 방송사명
	private final Program[] programs; // ★ Has-A : 방송사가 자기 편성표를 소유한다

	/**
	 * 프로그램 개수가 방송사마다 다르므로 가변인자로 받는다.
	 * (Program... 은 사실상 Program[] 이며, 호출할 때 배열을 만들지 않아도 된다)
	 */
	public Broadcaster(String name, Program... programs) {
		this.name = name;
		this.programs = programs;
	}

	/**
	 * 자기 편성표를 훑어 주어진 시각에 방영 중인 프로그램을 찾는다.
	 *
	 * @return 방영 중인 프로그램. 없으면 null
	 */
	public Program findProgramAt(LocalTime time) {

		for (Program program : this.programs) {
			// 방영 여부 판단은 프로그램 자신에게 맡긴다.
			if (program.isOnAir(time)) {
				return program;
			}
		}

		return null;
	}

	public String getName() {
		return this.name;
	}

}
