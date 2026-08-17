package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 도서 한 권.
 *
 * 같은 책(ISBN이 같은 책)이라도 도서관이 부여한 관리 고유번호는 서로 다르다.
 * 즉 ISBN은 "무슨 책인가", 관리번호는 "그 책 중 몇 번째 권인가"를 가리킨다.
 */
public class Book {

	/** 대여 기간(일) */
	public static final int RENTAL_DAYS = 7;

	/** 반납 예정일 며칠 전부터 "반납기간 도래"로 볼 것인가 */
	public static final int DUE_SOON_DAYS = 2;

	/** 폐기 기준. 출판 후 햇수로 이 값 이상 지나면 폐기한다. */
	public static final int DISPOSAL_YEARS = 10;

	// 도서 자체의 정보. 입고 후에는 바뀌지 않는다.
	private final int managementNo; // 관리 고유번호 (도서관이 부여, 중복 불가). 순번이므로 정수로 다룬다
	private final String isbn; // 책 고유번호 (같은 책이면 동일)
	private final String title; // 도서명
	private final String subtitle; // 도서 부제
	private final String genre; // 장르
	private final String publisher; // 출판사명
	private final String author; // 저자
	private final LocalDate publishDate; // 출판일
	private final int printCount; // 인쇄 회차
	private final LocalDate stockDate; // 입고일
	private final int price; // 가격

	// 대여 정보. 자바가 0 / false / null 로 초기화해주므로 따로 대입하지 않는다.
	private int rentalCount; // 총 대여횟수
	private boolean rented; // 대여상태
	private LocalDate rentalDate; // 대여일 (미대여면 null)
	private boolean returned; // 반납완료상태
	private LocalDate returnDate; // 실제 반납한 날짜 (미반납이면 null)
	private String renterName = ""; // 대여한 회원명. 기본값 null 대신 빈 문자열로 시작한다

	/**
	 * 신규 입고용 생성자. 도서 자체의 정보만 받는다.
	 */
	public Book(int managementNo, String isbn, String title, String subtitle, String genre, String publisher,
			String author, LocalDate publishDate, int printCount, LocalDate stockDate, int price) {
		this.managementNo = managementNo;
		this.isbn = isbn;
		this.title = title;
		this.subtitle = subtitle;
		this.genre = genre;
		this.publisher = publisher;
		this.author = author;
		this.publishDate = publishDate;
		this.printCount = printCount;
		this.stockDate = stockDate;
		this.price = price;
	}

	/**
	 * 파일에서 읽어올 때 쓰는 생성자.
	 * 도서 정보는 위 생성자에 맡기고, 대여 정보만 이어서 채운다.
	 */
	public Book(int managementNo, String isbn, String title, String subtitle, String genre, String publisher,
			String author, LocalDate publishDate, int printCount, LocalDate stockDate, int price, int rentalCount,
			boolean rented, LocalDate rentalDate, boolean returned, LocalDate returnDate, String renterName) {

		this(managementNo, isbn, title, subtitle, genre, publisher, author, publishDate, printCount, stockDate, price);

		this.rentalCount = rentalCount;
		this.rented = rented;
		this.rentalDate = rentalDate;
		this.returned = returned;
		this.returnDate = returnDate;
		this.renterName = renterName;
	}

	// ------------------------------------------------------------------
	// 날짜 판단
	// ------------------------------------------------------------------

	/**
	 * 반납 예정일. 저장하지 않고 대여일로부터 계산한다.
	 *
	 * @return 대여 중이 아니면 null
	 */
	public LocalDate getDueDate() {
		if (this.rentalDate == null) {
			return null;
		}
		return this.rentalDate.plusDays(RENTAL_DAYS);
	}

	/**
	 * 폐기 대상인지 판단한다. "햇수로 10년"이므로 날짜가 아니라 연도 차이로 센다.
	 * (2016년 출판 도서는 2026년이 되면 폐기 대상)
	 */
	public boolean isDisposalTarget(LocalDate today) {
		return today.getYear() - this.publishDate.getYear() >= DISPOSAL_YEARS;
	}

	/**
	 * 반납기간이 도래했는지 판단한다. 반납 예정일 이틀 전부터 예정일 당일까지가 해당된다.
	 */
	public boolean isDueSoon(LocalDate today) {
		if (!this.rented) {
			return false;
		}
		LocalDate dueDate = this.getDueDate();
		LocalDate noticeDate = dueDate.minusDays(DUE_SOON_DAYS);

		// noticeDate <= today <= dueDate
		return !today.isBefore(noticeDate) && !today.isAfter(dueDate);
	}

	/**
	 * 반납 예정일이 지났는지 판단한다.
	 */
	public boolean isOverdue(LocalDate today) {
		if (!this.rented) {
			return false;
		}
		return today.isAfter(this.getDueDate());
	}

	/**
	 * 연체 일수. 연체가 아니면 0을 반환한다.
	 */
	public long getOverdueDays(LocalDate today) {
		if (!this.isOverdue(today)) {
			return 0;
		}
		return ChronoUnit.DAYS.between(this.getDueDate(), today);
	}

	// ------------------------------------------------------------------
	// 대여 / 반납
	// ------------------------------------------------------------------

	/**
	 * 회원에게 대여한다. 총 대여횟수가 1 증가한다.
	 */
	public void rentTo(String memberName, LocalDate today) {
		this.rented = true;
		this.rentalDate = today;
		this.renterName = memberName;
		this.returned = false;
		this.returnDate = null;
		this.rentalCount++;
	}

	/**
	 * 반납 처리한다. 벌금과 초과 횟수 계산은 도서관이 담당한다.
	 */
	public void returnBy(LocalDate today) {
		this.rented = false;
		this.returned = true;
		this.returnDate = today;
		this.renterName = "";
	}

	// ------------------------------------------------------------------
	// CSV 변환
	// ------------------------------------------------------------------

	/** CSV 한 줄로 만든다. */
	public String toCsv() {
		return String.join(",",
				String.format("%04d", this.managementNo),
				this.isbn,
				this.title,
				this.subtitle,
				this.genre,
				this.publisher,
				this.author,
				dateToText(this.publishDate),
				String.valueOf(this.printCount),
				dateToText(this.stockDate),
				String.valueOf(this.price),
				String.valueOf(this.rentalCount),
				String.valueOf(this.rented),
				dateToText(this.rentalDate),
				String.valueOf(this.returned),
				dateToText(this.returnDate),
				this.renterName);
	}

	/** CSV 한 줄을 도서로 되돌린다. */
	public static Book parse(String line) {
		// -1을 주어야 줄 끝의 빈 칸까지 살아남는다. (미대여 도서는 마지막 칸이 비어 있다)
		String[] columns = line.split(",", -1);

		return new Book(
				Integer.parseInt(columns[0]),
				columns[1],
				columns[2],
				columns[3],
				columns[4],
				columns[5],
				columns[6],
				textToDate(columns[7]),
				Integer.parseInt(columns[8]),
				textToDate(columns[9]),
				Integer.parseInt(columns[10]),
				Integer.parseInt(columns[11]),
				Boolean.parseBoolean(columns[12]),
				textToDate(columns[13]),
				Boolean.parseBoolean(columns[14]),
				textToDate(columns[15]),
				columns[16]);
	}

	/** 날짜가 없으면 빈 칸으로 저장한다. */
	private static String dateToText(LocalDate date) {
		return date == null ? "" : date.toString();
	}

	/** 빈 칸은 날짜 없음으로 되돌린다. */
	private static LocalDate textToDate(String text) {
		return text.isBlank() ? null : LocalDate.parse(text.trim());
	}

	// ------------------------------------------------------------------
	// 출력
	// ------------------------------------------------------------------

	/** 목록에 한 줄로 출력할 때 쓴다. */
	public String toSummary() {
		String state = this.rented ? "대여중(" + this.renterName + ")" : "대여가능";
		return String.format("[%04d] %s - %s / %s / %s / %s / 대여 %d회 / %s",
				this.managementNo, this.title, this.subtitle, this.genre, this.author, this.publisher,
				this.rentalCount, state);
	}

	/** 검색 결과처럼 같은 책을 한 권만 보여줄 때 쓴다. (관리번호와 대여 상태를 뺀다) */
	public String toBookInfo() {
		return String.format("%s - %s / %s / %s / %s / %s 출판 / %,d원 (ISBN %s)",
				this.title, this.subtitle, this.genre, this.author, this.publisher, this.publishDate, this.price,
				this.isbn);
	}

	// ------------------------------------------------------------------
	// getter
	// ------------------------------------------------------------------

	public int getManagementNo() {
		return this.managementNo;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public String getTitle() {
		return this.title;
	}

	public String getGenre() {
		return this.genre;
	}

	public String getPublisher() {
		return this.publisher;
	}

	public String getAuthor() {
		return this.author;
	}

	public int getRentalCount() {
		return this.rentalCount;
	}

	public boolean isRented() {
		return this.rented;
	}

	public String getRenterName() {
		return this.renterName;
	}

}
