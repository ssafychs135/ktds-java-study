package library;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 도서관 정보를 CSV 파일로 읽고 쓴다.
 *
 * 경로가 src/library로 시작하는 이유는 이클립스에서 실행할 때 작업 디렉토리가
 * 프로젝트 루트(Homework)이기 때문이다.
 */
public class LibraryFileStore {

	private static final String DATA_DIR = "src/library/";

	private static final Path BOOKS_FILE = Path.of(DATA_DIR + "books.csv");
	private static final Path DISPOSED_FILE = Path.of(DATA_DIR + "disposed_books.csv");
	private static final Path MEMBERS_FILE = Path.of(DATA_DIR + "members.csv");

	private static final String BOOK_HEADER = "#관리번호,ISBN,도서명,부제,장르,출판사,저자,출판일,인쇄회차,입고일,가격,총대여횟수,대여상태,대여일,반납완료상태,반납일,대여회원명";
	private static final String MEMBER_HEADER = "#회원명,연락처,벌금,초과횟수,대여도서관리번호목록";

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private LibraryFileStore() {
	}

	/**
	 * 세 파일을 읽어 도서관을 구성한다.
	 *
	 * 회원의 대여 목록은 관리번호로만 저장되어 있으므로,
	 * 도서와 폐기 도서를 모두 합친 표에서 찾아 실제 객체 참조로 되돌린다.
	 */
	public static Library load() {

		Library library = new Library();

		List<Book> books = readLines(BOOKS_FILE).map(Book::parse).toList();
		List<Book> disposedBooks = readLines(DISPOSED_FILE).map(Book::parse).toList();

		books.forEach(library::addLoadedBook);
		disposedBooks.forEach(library::addLoadedDisposedBook);

		// 관리번호 -> 도서. 폐기 도서까지 넣어야 회원의 대여 목록을 복원할 수 있다.
		Map<String, Book> bookByNo = Stream.concat(books.stream(), disposedBooks.stream())
				.collect(Collectors.toMap(Book::getManagementNo, book -> book));

		readLines(MEMBERS_FILE)
				.map(line -> Member.parse(line, bookByNo))
				.forEach(library::addMember);

		return library;
	}

	/**
	 * 도서관 정보를 세 파일에 모두 저장한다. 데이터가 바뀔 때마다 호출한다.
	 */
	public static void save(Library library) {
		writeLines(BOOKS_FILE, BOOK_HEADER, library.getBooks().stream().map(Book::toCsv).toList());
		writeLines(DISPOSED_FILE, BOOK_HEADER, library.getDisposedBooks().stream().map(Book::toCsv).toList());
		writeLines(MEMBERS_FILE, MEMBER_HEADER, library.getMembers().stream().map(Member::toCsv).toList());
	}

	/**
	 * 파일을 읽어 의미 있는 줄만 남긴다. 파일이 없으면 빈 스트림을 준다.
	 */
	private static Stream<String> readLines(Path path) {

		if (!Files.exists(path)) {
			return Stream.empty();
		}

		try {
			// 파일을 닫기 위해 목록으로 받은 뒤 다시 스트림으로 만든다.
			return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
					.filter(line -> !line.isBlank())
					.filter(line -> !line.startsWith("#")); // 머리글 줄은 건너뛴다
		} catch (IOException exception) {
			throw new UncheckedIOException("파일을 읽을 수 없습니다 : " + path, exception);
		}
	}

	private static void writeLines(Path path, String header, List<String> lines) {

		List<String> output = new ArrayList<>();
		output.add(header);
		output.addAll(lines);

		try {
			Files.write(path, output, StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new UncheckedIOException("파일을 저장할 수 없습니다 : " + path, exception);
		}
	}

}
