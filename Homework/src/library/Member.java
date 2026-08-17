package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 도서관 회원.
 *
 * 회원은 자기가 대여한 도서 목록을 가진다(Has-A).
 * 이 목록의 Book은 도서관 도서 목록에 있는 것과 같은 객체다.
 * 복사본이 아니므로 한쪽에서 대여 상태를 바꾸면 양쪽에 함께 반영된다.
 */
public class Member {

	/** 이 횟수 이상 반납기간을 어기면 더 이상 대여할 수 없다. */
	public static final int RENTAL_BAN_COUNT = 3;

	/** 반납기간을 넘긴 하루마다 누적되는 벌금(원) */
	public static final int FINE_PER_DAY = 500;

	private final String name; // 회원명
	private final String phone; // 연락처

	// 벌금과 초과 횟수는 자바가 0으로 초기화해주므로 따로 대입하지 않는다.
	private int fine; // 벌금(원)
	private int overdueCount; // 반납기간 초과 횟수

	private final List<Book> rentedBooks = new ArrayList<>(); // ★ Has-A : 대여한 도서 목록

	/**
	 * 신규 가입용 생성자.
	 */
	public Member(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	/**
	 * 파일에서 읽어올 때 쓰는 생성자.
	 * 회원 정보는 위 생성자에 맡기고, 누적된 벌금과 초과 횟수만 이어서 채운다.
	 */
	public Member(String name, String phone, int fine, int overdueCount) {
		this(name, phone);

		this.fine = fine;
		this.overdueCount = overdueCount;
	}

	// ------------------------------------------------------------------
	// 대여 자격
	// ------------------------------------------------------------------

	/**
	 * 대여할 수 있는 회원인지 판단한다. 반납기간 초과 횟수가 3회 이상이면 대여할 수 없다.
	 */
	public boolean canRent() {
		return this.overdueCount < RENTAL_BAN_COUNT;
	}

	/**
	 * 상습 미반납 회원인지 판단한다. 대여 금지 기준과 같은 값을 쓴다.
	 */
	public boolean isHabitualOverdue() {
		return this.overdueCount >= RENTAL_BAN_COUNT;
	}

	// ------------------------------------------------------------------
	// 대여 목록 관리
	// ------------------------------------------------------------------

	public void addRentedBook(Book book) {
		this.rentedBooks.add(book);
	}

	public void removeRentedBook(Book book) {
		this.rentedBooks.remove(book);
	}

	/**
	 * 관리번호로 대여 중인 도서를 찾는다.
	 *
	 * @return 못 찾으면 null
	 */
	public Book findRentedBook(String managementNo) {
		return this.rentedBooks.stream()
				.filter(book -> book.getManagementNo().equals(managementNo))
				.findFirst()
				.orElse(null);
	}

	// ------------------------------------------------------------------
	// 연체 처리
	// ------------------------------------------------------------------

	/**
	 * 연체 일수만큼 벌금을 매기고 초과 횟수를 1 올린다.
	 *
	 * @return 이번에 부과된 벌금
	 */
	public int chargeOverdue(long overdueDays) {
		int charged = (int) (overdueDays * FINE_PER_DAY);
		this.fine += charged;
		this.overdueCount++;
		return charged;
	}

	// ------------------------------------------------------------------
	// CSV 변환
	// ------------------------------------------------------------------

	/**
	 * CSV 한 줄로 만든다.
	 * 대여 도서는 관리번호만 세미콜론으로 이어 붙인다. (쉼표는 필드 구분자라 쓸 수 없다)
	 */
	public String toCsv() {
		String rentedBookNos = this.rentedBooks.stream()
				.map(Book::getManagementNo)
				.reduce((joined, managementNo) -> joined + ";" + managementNo)
				.orElse("");

		return String.join(",", this.name, this.phone, String.valueOf(this.fine),
				String.valueOf(this.overdueCount), rentedBookNos);
	}

	/**
	 * CSV 한 줄을 회원으로 되돌린다.
	 *
	 * @param bookByNo 관리번호로 도서를 찾기 위한 표. 폐기 도서까지 포함해야 한다.
	 */
	public static Member parse(String line, Map<String, Book> bookByNo) {
		String[] columns = line.split(",", -1);

		Member member = new Member(
				columns[0],
				columns[1],
				Integer.parseInt(columns[2]),
				Integer.parseInt(columns[3]));

		// 관리번호 목록을 실제 Book 객체 참조로 되돌린다.
		String rentedBookNos = columns[4];
		if (!rentedBookNos.isBlank()) {
			for (String managementNo : rentedBookNos.split(";")) {
				Book book = bookByNo.get(managementNo.trim());
				if (book != null) {
					member.addRentedBook(book);
				}
			}
		}

		return member;
	}

	// ------------------------------------------------------------------
	// 출력
	// ------------------------------------------------------------------

	public String toSummary() {
		return String.format("%s (%s) / 대여 %d권 / 초과 %d회 / 벌금 %,d원",
				this.name, this.phone, this.rentedBooks.size(), this.overdueCount, this.fine);
	}

	/**
	 * 반납기간 도래·초과 상황을 함께 보여준다.
	 */
	public String toOverdueSummary(LocalDate today, List<Book> disposedBooks) {
		StringBuilder summary = new StringBuilder(this.toSummary());

		for (Book book : this.rentedBooks) {
			String state;
			if (disposedBooks.contains(book)) {
				state = "폐기됨(반납 불필요)";
			} else if (book.isOverdue(today)) {
				state = "연체 " + book.getOverdueDays(today) + "일";
			} else if (book.isDueSoon(today)) {
				state = "반납기간 도래 (예정일 " + book.getDueDate() + ")";
			} else {
				state = "대여중 (예정일 " + book.getDueDate() + ")";
			}
			summary.append(String.format("%n     - [%s] %s : %s", book.getManagementNo(), book.getTitle(), state));
		}

		return summary.toString();
	}

	// ------------------------------------------------------------------
	// getter
	// ------------------------------------------------------------------

	public String getName() {
		return this.name;
	}

	public int getFine() {
		return this.fine;
	}

	public int getOverdueCount() {
		return this.overdueCount;
	}

	/** 대여 도서 목록. 외부에서 직접 고치지 못하게 복사본을 준다. */
	public List<Book> getRentedBooks() {
		return new ArrayList<>(this.rentedBooks);
	}

}
