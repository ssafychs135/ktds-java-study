package board.utils;

import java.util.Scanner;

/**
 * 터미널 입력을 공통으로 처리해주는 유틸리티
 */
public class ScannerUtil {

	private final static Scanner sc;

	static {
		sc = new Scanner(System.in);
	}

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private ScannerUtil() {
	}

	/**
	 * 안내 문구를 출력하고 한 줄을 입력받는다.
	 */
	public static String nextLine(String prompt) {
		System.out.print(prompt);
		return sc.nextLine();
	}

	/**
	 * 안내 문구를 출력하고 정수를 입력받는다. 잘못된 값을 입력하면 -1을 반환하여 오류 핸들링
	 */
	public static int nextInt(String prompt) {
		System.out.print(prompt);
		try {
			return Integer.parseInt(sc.nextLine().trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void close() {
		sc.close();
	}

}
