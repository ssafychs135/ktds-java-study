package broadcast;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * 편성표 애플리케이션
 *
 * 매일 같은 시간에 같은 프로그램이 방영되는 채널의 편성표를 두고,
 * 입력받은 시각에 어떤 프로그램이 방영 중인지 출력한다.
 */
public class BroadcastMain {

	/**
	 * 입력 시각의 형식. 'H'는 한 자리 시각도 허용하므로 "1:11"과 "01:11"을 모두 받는다.
	 * ('HH'로 두면 "1:11"이 예외가 된다.)
	 */
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

	/**
	 * 편성표. { 시작시각, 종료시각, 프로그램명 } 순서이며 양 끝 시각을 모두 포함한다.
	 * 00:00 ~ 02:59 는 방영하는 프로그램이 없다.
	 */
	private static final String[][] SCHEDULE = {
			{ "03:00", "05:00", "프로그램 1" },
			{ "05:01", "06:30", "프로그램 2" },
			{ "06:31", "08:00", "프로그램 3" },
			{ "08:01", "09:00", "프로그램 4" },
			{ "09:01", "10:00", "프로그램 5" },
			{ "10:01", "12:00", "프로그램 6" },
			{ "12:01", "13:30", "프로그램 7" },
			{ "13:31", "15:00", "프로그램 8" },
			{ "15:01", "17:00", "프로그램 9" },
			{ "17:01", "18:00", "프로그램 10" },
			{ "18:01", "21:00", "프로그램 11" },
			{ "21:01", "23:00", "프로그램 12" },
			{ "23:01", "23:59", "프로그램 13" },
	};

	private static final String NOT_ON_AIR = "방영중인 시간이 아닙니다.";

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("시각을 입력하세요 (HH:mm, 그냥 Enter 시 종료) : ");

			// 입력 스트림이 끝난 경우. 검사하지 않으면 nextLine()이 예외를 던진다.
			if (!scanner.hasNextLine()) {
				System.out.println();
				System.out.println("프로그램을 종료합니다.");
				break;
			}

			String input = scanner.nextLine().trim();

			// 아무것도 입력하지 않으면 종료한다.
			if (input.isEmpty()) {
				System.out.println("프로그램을 종료합니다.");
				break;
			}

			try {
				LocalTime time = LocalTime.parse(input, TIME_FORMAT);
				System.out.println(findProgramName(time));
			} catch (DateTimeParseException exception) {
				// 형식이 어긋나거나 25:99 처럼 존재하지 않는 시각인 경우
				System.out.println("시각 형식이 잘못되었습니다. 예) 10:34");
			}

			System.out.println();
		}

		scanner.close();
	}

	/**
	 * 편성표를 위에서부터 훑어 주어진 시각에 방영 중인 프로그램을 찾는다.
	 *
	 * @param time 조회할 시각
	 * @return 방영 중인 프로그램명. 방영 중인 프로그램이 없으면 안내 문구
	 */
	private static String findProgramName(LocalTime time) {

		for (String[] row : SCHEDULE) {
			LocalTime startTime = LocalTime.parse(row[0]);
			LocalTime endTime = LocalTime.parse(row[1]);

			// LocalTime에는 isBeforeOrEqual이 없으므로 부정으로 뒤집어 양 끝을 포함시킨다.
			// (startTime <= time && time <= endTime 과 같은 뜻)
			if (!time.isBefore(startTime) && !time.isAfter(endTime)) {
				return row[2];
			}
		}

		return NOT_ON_AIR;
	}

}
