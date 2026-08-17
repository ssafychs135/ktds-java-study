package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 도서관.
 *
 * 도서 목록과 회원 목록을 소유한다(Has-A).
 * 폐기 도서 목록은 "폐기됐지만 아직 회원이 대여 중인 책"만 담는다.
 */
public class Library {

	private final List<Book> books = new ArrayList<>(); // ★ Has-A : 도서 목록
	private final List<Book> disposedBooks = new ArrayList<>(); // 폐기됐지만 대여 중인 도서
	private final List<Member> members = new ArrayList<>(); // ★ Has-A : 회원 목록

	// ------------------------------------------------------------------
	// 1. 신규 도서 입고
	// ------------------------------------------------------------------

	/**
	 * 신규 도서를 입고한다. 관리 고유번호는 기존 최대값 다음 번호로 자동 부여한다.
	 */
	public Book addBook(String isbn, String title, String subtitle, String genre, String publisher, String author,
			LocalDate publishDate, int printCount, LocalDate stockDate, int price) {

		Book book = new Book(this.nextManagementNo(), isbn, title, subtitle, genre, publisher, author, publishDate,
				printCount, stockDate, price);
		this.books.add(book);
		return book;
	}

	/**
	 * 다음 관리 고유번호를 만든다. 폐기 도서 번호까지 살펴서 재사용되지 않게 한다.
	 */
	private String nextManagementNo() {
		int maxNo = Stream.concat(this.books.stream(), this.disposedBooks.stream())
				.map(Book::getManagementNo)
				.map(managementNo -> managementNo.substring(1)) // "B0007" -> "0007"
				.mapToInt(Integer::parseInt)
				.max()
				.orElse(0);

		return String.format("B%04d", maxNo + 1);
	}

	// ------------------------------------------------------------------
	// 2. 10년 경과 도서 폐기
	// ------------------------------------------------------------------

	/**
	 * 출판된 지 햇수로 10년이 지난 도서를 모두 폐기한다.
	 *
	 * 폐기 도서는 도서 목록에서 빠진다. 다만 회원이 대여 중이라면 회원의 대여 목록에는
	 * 그대로 남는다(같은 객체를 참조하므로). 그 책들만 disposedBooks에 따로 담아둔다.
	 *
	 * @return 폐기된 도서 목록
	 */
	public List<Book> disposeOldBooks(LocalDate today) {

		List<Book> targets = this.books.stream()
				.filter(book -> book.isDisposalTarget(today))
				.toList();

		this.books.removeAll(targets);

		// 아직 대여 중인 폐기 도서는 회원 쪽에서 참조하므로 따로 보관한다.
		targets.stream()
				.filter(Book::isRented)
				.forEach(this.disposedBooks::add);

		return targets;
	}

	// ------------------------------------------------------------------
	// 3. 반납기간 도래·초과 회원 조회
	// ------------------------------------------------------------------

	/**
	 * 반납기간이 도래했거나 지난 회원을 찾는다.
	 *
	 * 폐기된 도서는 반납할 필요가 없으므로 판단에서 제외한다.
	 * 따라서 폐기 도서만 대여 중인 회원은 조회되지 않고,
	 * 폐기 도서와 정상 도서를 함께 대여 중이라면 조회된다.
	 */
	public List<Member> findDueOrOverdueMembers(LocalDate today) {
		return this.members.stream()
				.filter(member -> member.getRentedBooks().stream()
						.filter(book -> !this.disposedBooks.contains(book))
						.anyMatch(book -> book.isDueSoon(today) || book.isOverdue(today)))
				.toList();
	}

	// ------------------------------------------------------------------
	// 4~5. 인기 / 비인기 도서
	// ------------------------------------------------------------------

	/** 총 대여횟수가 많은 순 상위 몇 권 */
	public List<Book> findPopularBooks(int limit) {
		return this.books.stream()
				.sorted(Comparator.comparingInt(Book::getRentalCount).reversed())
				.limit(limit)
				.toList();
	}

	/** 총 대여횟수가 적은 순 하위 몇 권 */
	public List<Book> findUnpopularBooks(int limit) {
		return this.books.stream()
				.sorted(Comparator.comparingInt(Book::getRentalCount))
				.limit(limit)
				.toList();
	}

	// ------------------------------------------------------------------
	// 6. 상습 미반납 회원
	// ------------------------------------------------------------------

	public List<Member> findHabitualOverdueMembers() {
		return this.members.stream()
				.filter(Member::isHabitualOverdue)
				.toList();
	}

	// ------------------------------------------------------------------
	// 9. 도서 검색
	// ------------------------------------------------------------------

	/**
	 * 출판사명 / 저자 / 장르로 도서를 검색한다.
	 *
	 * 대여 상태와 무관하게 모두 조회하되, 같은 책(ISBN이 같은 책)은 한 권만 남긴다.
	 *
	 * @param type 1=출판사명, 2=저자, 3=장르
	 */
	public List<Book> searchBooks(int type, String keyword) {

		Stream<Book> matched = this.books.stream().filter(book -> {
			if (type == 1) {
				return book.getPublisher().contains(keyword);
			} else if (type == 2) {
				return book.getAuthor().contains(keyword);
			} else {
				return book.getGenre().contains(keyword);
			}
		});

		// ISBN을 키로 모으면 같은 책은 자연히 하나만 남는다.
		// 충돌 시 먼저 나온 책을 유지하고, LinkedHashMap으로 순서를 지킨다.
		return new ArrayList<>(matched
				.collect(Collectors.toMap(Book::getIsbn, book -> book, (existing, duplicate) -> existing, LinkedHashMap::new))
				.values());
	}

	// ------------------------------------------------------------------
	// 10. 도서 대여
	// ------------------------------------------------------------------

	/**
	 * 회원에게 도서를 대여한다. 같은 ISBN의 책 중 대여 가능한 첫 권을 배정한다.
	 */
	public Book rentBook(String memberName, String isbn, LocalDate today) {

		Member member = this.findMember(memberName);

		if (!member.canRent()) {
			throw new LibraryException(memberName + " 회원은 반납기간 초과가 " + member.getOverdueCount() + "회이므로 대여할 수 없습니다.");
		}

		Book book = this.books.stream()
				.filter(candidate -> candidate.getIsbn().equals(isbn))
				.filter(candidate -> !candidate.isRented())
				.findFirst()
				.orElseThrow(() -> new LibraryException("대여 가능한 도서가 없습니다. (ISBN " + isbn + ")"));

		book.rentTo(member.getName(), today);
		member.addRentedBook(book);
		return book;
	}

	// ------------------------------------------------------------------
	// 11. 도서 반납
	// ------------------------------------------------------------------

	/**
	 * 도서를 반납한다.
	 *
	 * 반납기간을 넘겼으면 초과 횟수가 1 늘고 하루당 500원의 벌금이 붙는다.
	 * 다만 폐기된 도서는 반납할 필요가 없는 책이므로 벌금도 초과 횟수도 발생하지 않는다.
	 *
	 * @return 이번에 부과된 벌금. 없으면 0
	 */
	public int returnBook(String memberName, String managementNo, LocalDate today) {

		Member member = this.findMember(memberName);

		Book book = member.findRentedBook(managementNo);
		if (book == null) {
			throw new LibraryException(memberName + " 회원이 대여 중인 도서가 아닙니다. (관리번호 " + managementNo + ")");
		}

		boolean disposed = this.disposedBooks.contains(book);
		int charged = 0;

		if (!disposed && book.isOverdue(today)) {
			charged = member.chargeOverdue(book.getOverdueDays(today));
		}

		book.returnBy(today);
		member.removeRentedBook(book);

		// 폐기된 책은 반납되는 순간 어디에도 남기지 않는다.
		if (disposed) {
			this.disposedBooks.remove(book);
		}

		return charged;
	}

	// ------------------------------------------------------------------
	// 조회 보조
	// ------------------------------------------------------------------

	public Member findMember(String name) {
		return this.members.stream()
				.filter(member -> member.getName().equals(name))
				.findFirst()
				.orElseThrow(() -> new LibraryException("존재하지 않는 회원입니다. (" + name + ")"));
	}

	public void addMember(Member member) {
		this.members.add(member);
	}

	public void addLoadedBook(Book book) {
		this.books.add(book);
	}

	public void addLoadedDisposedBook(Book book) {
		this.disposedBooks.add(book);
	}

	public List<Book> getBooks() {
		return new ArrayList<>(this.books);
	}

	public List<Book> getDisposedBooks() {
		return new ArrayList<>(this.disposedBooks);
	}

	public List<Member> getMembers() {
		return new ArrayList<>(this.members);
	}

}
