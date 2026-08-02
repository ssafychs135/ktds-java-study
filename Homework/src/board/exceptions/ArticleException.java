package board.exceptions;

/**
 * 게시글의 제목이 규칙에 맞지 않을 때 발생하는 예외.
 */
public class ArticleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ArticleException(String message) {
		super(message);
	}

}
