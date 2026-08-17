package library;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * 터미널 입력을 한 곳에서 처리한다.
 *
 * 올바른 값이 들어올 때까지 다시 묻기 때문에, 부르는 쪽은 검증을 신경 쓰지 않아도 된다.
 */
public class InputUtil {

	private static final Scanner scanner = new Scanner(System.in);

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private InputUtil() {
	}

	/**
	 * 빈 값과 쉼표를 막는다. 쉼표가 들어가면 CSV 저장 형식이 깨진다.
	 */
	public static String nextText(String prompt) {
		while (true) {
			System.out.print(prompt);
			String value = scanner.nextLine().trim();

			if (value.isBlank()) {
				System.out.println("  값을 입력해야 합니다.");
			} else if (value.contains(",")) {
				System.out.println("  쉼표(,)는 사용할 수 없습니다. 저장 형식이 깨집니다.");
			} else {
				return value;
			}
		}
	}

	public static int nextInt(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				return Integer.parseInt(scanner.nextLine().trim());
			} catch (NumberFormatException exception) {
				System.out.println("  숫자를 입력해야 합니다.");
			}
		}
	}

	public static LocalDate nextDate(String prompt) {
		while (true) {
			System.out.print(prompt);
			try {
				return LocalDate.parse(scanner.nextLine().trim());
			} catch (DateTimeParseException exception) {
				System.out.println("  날짜 형식이 잘못되었습니다. 예) 2020-03-15");
			}
		}
	}

	public static void close() {
		scanner.close();
	}

}
