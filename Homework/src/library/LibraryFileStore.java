package library;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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

	// 파일에는 데이터만 담는다. 칸 순서는 README.md에 적어 두었다.

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private LibraryFileStore() {
	}

	/**
	 * 세 파일을 읽어 도서관을 구성한다.
	 *
	 * 회원의 대여 목록은 파일에 저장하지 않는다. 대신 도서의 "대여회원명"을 보고
	 * 대여 중인 도서를 회원에게 되돌려 붙인다. 폐기 도서도 회원이 들고 있을 수 있으므로
	 * 두 목록을 합쳐서 살펴본다.
	 */
	public static Library load() {

		Library library = new Library();

		List<Book> books = readLines(BOOKS_FILE) // Stream<String>
				.map(Book::parse) //               Stream<Book>
				.toList(); //                      List<Book>

		List<Book> disposedBooks = readLines(DISPOSED_FILE) // Stream<String>
				.map(Book::parse) //                          Stream<Book>
				.toList(); //                                 List<Book>

		books.forEach(library::addLoadedBook);
		disposedBooks.forEach(library::addLoadedDisposedBook);

		List<Member> members = readLines(MEMBERS_FILE) // Stream<String>
				.map(Member::parse) //                   Stream<Member>
				.toList(); //                            List<Member>

		members.forEach(library::addMember);

		// 대여 관계를 되살린다. 회원마다 자기 이름으로 대여된 도서를 찾아 목록에 넣는다.
		List<Book> allBooks = Stream.concat(books.stream(), disposedBooks.stream()) // Stream<Book>
				.toList(); //                                                         List<Book>

		for (Member member : members) {
			allBooks.stream() // Stream<Book>
					.filter(book -> book.isRented()
							&& book.getRenterName().equals(member.getName())) // Stream<Book>
					.forEach(member::addRentedBook);
		}

		return library;
	}

	/**
	 * 도서관 정보를 세 파일에 모두 저장한다. 데이터가 바뀔 때마다 호출한다.
	 */
	public static void save(Library library) {
		// 세 줄 모두 Stream<Book 또는 Member> -> map -> Stream<String>(CSV 한 줄) -> List<String>
		writeLines(BOOKS_FILE, library.getBooks().stream().map(Book::toCsv).toList());
		writeLines(DISPOSED_FILE, library.getDisposedBooks().stream().map(Book::toCsv).toList());
		writeLines(MEMBERS_FILE, library.getMembers().stream().map(Member::toCsv).toList());
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
			return Files.readAllLines(path, StandardCharsets.UTF_8).stream() // Stream<String>
					.filter(line -> !line.isBlank()); //                       Stream<String>  빈 줄 제외
		} catch (IOException exception) {
			throw new UncheckedIOException("파일을 읽을 수 없습니다 : " + path, exception);
		}
	}

	private static void writeLines(Path path, List<String> lines) {
		try {
			Files.write(path, lines, StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new UncheckedIOException("파일을 저장할 수 없습니다 : " + path, exception);
		}
	}

}
