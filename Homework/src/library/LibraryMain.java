package library;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 도서관 관리 프로그램의 시작점.
 *
 * 입력은 InputUtil, 출력은 LibraryView, 업무 규칙은 Library, 저장은 LibraryFileStore가 맡는다.
 * 이 클래스에는 "무엇을 어떤 순서로 하는가"라는 흐름만 남는다.
 */
public class LibraryMain {

	public static void main(String[] args) {

		Library library = LibraryFileStore.load();
		LocalDate today = LocalDate.now();

		try {
			while (true) {
				LibraryView.printMenu(today);
				int menuNo = InputUtil.nextInt("메뉴 선택 : ");

				if (menuNo == 0) {
					LibraryView.printMessage("프로그램을 종료합니다.");
					break;
				}

				try {
					runMenu(library, menuNo, today);
				} catch (LibraryException exception) {
					// 업무 규칙 위반은 안내만 하고 메뉴로 돌아간다.
					LibraryView.printMessage("[알림] " + exception.getMessage());
				}

				LibraryView.printBlankLine();
			}
		} catch (NoSuchElementException exception) {
			// 파이프 입력 등으로 더 읽을 것이 없는 경우
			LibraryView.printBlankLine();
			LibraryView.printMessage("입력이 종료되어 프로그램을 마칩니다.");
		}

		InputUtil.close();
	}

	private static void runMenu(Library library, int menuNo, LocalDate today) {

		if (menuNo == 1) {
			addBook(library, today);
		} else if (menuNo == 2) {
			disposeOldBooks(library, today);
		} else if (menuNo == 3) {
			LibraryView.printDueOrOverdueMembers(library.findDueOrOverdueMembers(today), today,
					library.getDisposedBooks());
		} else if (menuNo == 4) {
			LibraryView.printBooks("인기 도서 (대여 많은 순)", library.findPopularBooks(5));
		} else if (menuNo == 5) {
			LibraryView.printBooks("비인기 도서 (대여 적은 순)", library.findUnpopularBooks(5));
		} else if (menuNo == 6) {
			LibraryView.printMembers("상습 미반납 회원 (초과 " + Member.RENTAL_BAN_COUNT + "회 이상)",
					library.findHabitualOverdueMembers());
		} else if (menuNo == 7) {
			LibraryView.printBooks("전체 도서 목록", library.getBooks());
		} else if (menuNo == 8) {
			LibraryView.printMembers("전체 회원 목록", library.getMembers());
		} else if (menuNo == 9) {
			searchBooks(library);
		} else if (menuNo == 10) {
			rentBook(library, today);
		} else if (menuNo == 11) {
			returnBook(library, today);
		} else {
			LibraryView.printMessage("잘못된 입력입니다.");
		}
	}

	/** 1. 신규 도서 입고 */
	private static void addBook(Library library, LocalDate today) {

		LibraryView.printTitle("신규 도서 입고");

		String isbn = InputUtil.nextText("ISBN : ");
		String title = InputUtil.nextText("도서명 : ");
		String subtitle = InputUtil.nextText("도서 부제 : ");
		String genre = InputUtil.nextText("장르 : ");
		String publisher = InputUtil.nextText("출판사명 : ");
		String author = InputUtil.nextText("저자 : ");
		LocalDate publishDate = InputUtil.nextDate("출판일 (YYYY-MM-DD) : ");
		int printCount = InputUtil.nextInt("인쇄 회차 : ");
		int price = InputUtil.nextInt("가격 : ");

		Book book = library.addBook(isbn, title, subtitle, genre, publisher, author, publishDate, printCount, today,
				price);

		LibraryFileStore.save(library);
		LibraryView.printAddedBook(book);
	}

	/** 2. 10년 경과 도서 폐기 */
	private static void disposeOldBooks(Library library, LocalDate today) {

		List<Book> disposed = library.disposeOldBooks(today);

		if (!disposed.isEmpty()) {
			LibraryFileStore.save(library);
		}

		LibraryView.printDisposedBooks(disposed);
	}

	/** 9. 도서 검색 */
	private static void searchBooks(Library library) {

		LibraryView.printMessage("[도서 검색]  1.출판사명  2.저자  3.장르");
		int type = InputUtil.nextInt("검색 기준 : ");

		if (type < 1 || type > 3) {
			LibraryView.printMessage("잘못된 검색 기준입니다.");
			return;
		}

		LibraryView.printSearchResult(library.searchBooks(type, InputUtil.nextText("검색어 : ")));
	}

	/** 10. 도서 대여 */
	private static void rentBook(Library library, LocalDate today) {

		LibraryView.printTitle("도서 대여");

		String memberName = InputUtil.nextText("회원명 : ");
		String isbn = InputUtil.nextText("대여할 도서의 ISBN : ");

		Book book = library.rentBook(memberName, isbn, today);

		LibraryFileStore.save(library);
		LibraryView.printRentResult(book);
	}

	/** 11. 도서 반납 */
	private static void returnBook(Library library, LocalDate today) {

		LibraryView.printTitle("도서 반납");

		String memberName = InputUtil.nextText("회원명 : ");
		Member member = library.findMember(memberName);

		if (member.getRentedBooks().isEmpty()) {
			LibraryView.printMessage("대여 중인 도서가 없습니다.");
			return;
		}

		LibraryView.printRentedBooksOf(member, library.getDisposedBooks());

		int charged = library.returnBook(memberName, InputUtil.nextText("반납할 도서의 관리번호 : "), today);

		LibraryFileStore.save(library);
		LibraryView.printReturnResult(charged, member);
	}

}
