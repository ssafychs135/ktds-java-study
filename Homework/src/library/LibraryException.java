package library;

/**
 * 업무 규칙을 어겼을 때 던지는 예외.
 * 메뉴 루프에서 잡아 안내 문구만 출력하고 프로그램은 계속 돈다.
 *
 * RuntimeException을 상속했으므로 호출하는 쪽에서 throws를 붙이지 않아도 된다.
 */
public class LibraryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LibraryException(String message) {
		super(message);
	}

}
