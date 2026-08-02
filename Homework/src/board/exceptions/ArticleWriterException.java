package board.exceptions;

/**
 * 게시글의 작성자 이름을 입력하지 않았을 때 발생하는 예외.
 */
public class ArticleWriterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ArticleWriterException(String message) {
		super(message);
	}

}
