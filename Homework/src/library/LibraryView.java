package library;

import java.time.LocalDate;
import java.util.List;

/**
 * 화면 출력을 한 곳에서 처리한다.
 *
 * 이 클래스만 System.out을 쓰므로, 출력 문구를 바꿀 일이 생기면 여기만 보면 된다.
 */
public class LibraryView {

	private static final String LINE = "--------------------------------------------------";

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private LibraryView() {
	}

	public static void printMenu(LocalDate today) {
		System.out.println(LINE);
		System.out.println("도서관 관리 프로그램   (오늘 " + today + ")");
		System.out.println(LINE);
		System.out.println(" [도서관]  1.신규 입고  2.10년 경과 폐기  3.반납기간 도래·초과 회원");
		System.out.println("           4.인기 도서   5.비인기 도서     6.상습 미반납 회원");
		System.out.println("           7.전체 도서   8.전체 회원");
		System.out.println(" [회원]    9.도서 검색  10.도서 대여      11.도서 반납");
		System.out.println("           0.종료");
	}

	public static void printTitle(String title) {
		System.out.println("[" + title + "]");
	}

	public static void printMessage(String message) {
		System.out.println(message);
	}

	public static void printBlankLine() {
		System.out.println();
	}

	// ------------------------------------------------------------------
	// 목록 출력
	// ------------------------------------------------------------------

	public static void printBooks(String title, List<Book> books) {
		printTitle(title);
		if (books.isEmpty()) {
			System.out.println("  도서가 없습니다.");
			return;
		}
		for (Book book : books) {
			System.out.println("  " + book.toSummary());
		}
	}

	public static void printMembers(String title, List<Member> members) {
		printTitle(title);
		if (members.isEmpty()) {
			System.out.println("  해당하는 회원이 없습니다.");
			return;
		}
		for (Member member : members) {
			System.out.println("  " + member.toSummary());
		}
	}

	// ------------------------------------------------------------------
	// 기능별 결과 출력
	// ------------------------------------------------------------------

	public static void printAddedBook(Book book) {
		System.out.printf("입고 완료. 관리 고유번호는 %04d 입니다.%n", book.getManagementNo());
	}

	public static void printDisposedBooks(List<Book> disposed) {
		if (disposed.isEmpty()) {
			System.out.println("폐기할 도서가 없습니다.");
			return;
		}

		printTitle("폐기 처리된 도서 " + disposed.size() + "권");
		for (Book book : disposed) {
			String note = book.isRented() ? "  ※ " + book.getRenterName() + " 대여중 (반납 불필요)" : "";
			System.out.println("  " + book.toSummary() + note);
		}
	}

	public static void printDueOrOverdueMembers(List<Member> targets, LocalDate today, List<Book> disposedBooks) {
		printTitle("반납기간 도래·초과 회원");
		if (targets.isEmpty()) {
			System.out.println("  해당하는 회원이 없습니다.");
			return;
		}
		for (Member member : targets) {
			System.out.println("  " + member.toOverdueSummary(today, disposedBooks));
		}
	}

	public static void printSearchResult(List<Book> found) {
		System.out.println("[검색 결과 " + found.size() + "건]  (같은 책은 한 권만 표시)");
		if (found.isEmpty()) {
			System.out.println("  검색 결과가 없습니다.");
			return;
		}
		for (Book book : found) {
			System.out.println("  " + book.toBookInfo());
		}
	}

	public static void printRentedBooksOf(Member member, List<Book> disposedBooks) {
		System.out.println("대여 중인 도서 :");
		for (Book book : member.getRentedBooks()) {
			String state = disposedBooks.contains(book) ? " (폐기됨)" : " (반납예정 " + book.getDueDate() + ")";
			System.out.printf("  [%04d] %s%s%n", book.getManagementNo(), book.getTitle(), state);
		}
	}

	public static void printRentResult(Book book) {
		System.out.printf("대여 완료 : [%04d] %s%n", book.getManagementNo(), book.getTitle());
		System.out.println("반납 예정일은 " + book.getDueDate() + " 입니다.");
	}

	public static void printReturnResult(int charged, Member member) {
		if (charged > 0) {
			System.out.printf("반납 완료. 연체로 벌금 %,d원이 부과되었습니다. (누적 %,d원, 초과 %d회)%n",
					charged, member.getFine(), member.getOverdueCount());
		} else {
			System.out.println("반납 완료. 부과된 벌금이 없습니다.");
		}
	}

}
