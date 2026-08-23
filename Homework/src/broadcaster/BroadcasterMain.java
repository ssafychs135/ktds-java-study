package broadcaster;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * 방송사별 편성표 애플리케이션
 *
 * 방송사마다 서로 다른 편성표를 가진다(Has-A).
 * 입력받은 시각에 각 방송사가 무엇을 방영 중인지 한 번에 출력한다.
 */
public class BroadcasterMain {

	/** 입력받을 시각의 형식. 'H'는 한 자리 시각도 허용한다. ("9:00", "09:00" 모두 가능) */
	private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("H:mm");

	/** 출력할 시각의 형식. 항상 두 자리로 맞춘다. (9:00 -> "09:00") */
	private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

	/**
	 * 방송사 목록. 방송사마다 자기 편성표를 들고 있다.
	 */
	private static final Broadcaster[] BROADCASTERS = {

			new Broadcaster("SBS",
					new Program("좋은아침", "06:00", "09:30"),
					new Program("모닝와이드", "09:31", "11:00"),
					new Program("생방송 투데이", "11:01", "13:00"),
					new Program("열린 TV", "13:01", "15:00")),

			new Broadcaster("KBS 1",
					new Program("아침마당", "08:30", "10:00"),
					new Program("사랑의 가족", "10:01", "12:00"),
					new Program("KBS 뉴스", "12:01", "14:00"),
					new Program("인간극장", "14:01", "16:00")),

			new Broadcaster("KBS 2",
					new Program("생생정보", "08:00", "09:40"),
					new Program("2TV 저녁", "09:41", "11:30"),
					new Program("해피투게더", "11:31", "13:00"),
					new Program("뮤직뱅크", "13:01", "15:00")),

			new Broadcaster("MBC",
					new Program("드라마", "07:50", "09:20"),
					new Program("기분좋은 날", "09:21", "11:00"),
					new Program("생방송 오늘아침", "11:01", "13:00"),
					new Program("무한도전", "13:01", "15:00")),

			new Broadcaster("EBS",
					new Program("애니메이션", "08:00", "09:30"),
					new Program("딩동댕 유치원", "09:31", "11:00"),
					new Program("세계테마기행", "11:01", "13:00"),
					new Program("다큐프라임", "13:01", "15:00")),

			// TVN은 10:00부터 방송을 시작하므로 09:00에는 방영 중인 프로그램이 없다.
			new Broadcaster("TVN",
					new Program("드라마", "10:00", "12:00"),
					new Program("예능", "12:01", "14:00"),
					new Program("영화", "14:01", "16:00")),
	};

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

			if (input.isEmpty()) {
				System.out.println("프로그램을 종료합니다.");
				break;
			}

			try {
				LocalTime time = LocalTime.parse(input, INPUT_FORMAT);
				printAllBroadcasters(time);
			} catch (DateTimeParseException exception) {
				// 형식이 어긋나거나 25:99 처럼 존재하지 않는 시각인 경우
				System.out.println("시각 형식이 잘못되었습니다. 예) 09:00");
			}

			System.out.println();
		}

		scanner.close();
	}

	/**
	 * 주어진 시각에 모든 방송사가 무엇을 방영 중인지 출력한다.
	 */
	private static void printAllBroadcasters(LocalTime time) {

		System.out.println();
		System.out.println("현재 시간 " + time.format(OUTPUT_FORMAT));

		for (Broadcaster broadcaster : BROADCASTERS) {
			// 방송사에게 "지금 뭐 방영해?"라고 묻는다. 찾는 방법은 방송사가 알아서 한다.
			Program program = broadcaster.findProgramAt(time);

			if (program == null) {
				System.out.println(broadcaster.getName() + " 방영중인 시간이 아닙니다.");
			} else {
				System.out.println(broadcaster.getName() + " “" + program.getName() + "” 이(가) 방영중입니다.");
			}
		}
	}

}
