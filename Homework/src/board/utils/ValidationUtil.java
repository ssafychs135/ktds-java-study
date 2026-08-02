package board.utils;

import board.exceptions.ArticleException;
import board.exceptions.ArticleWriterException;

public class ValidationUtil {

	/** 게시글 제목의 최대 길이 */
	public static final int MAX_TITLE_LENGTH = 30;

	// 객체를 만들 필요가 없는 유틸리티 클래스이므로 생성자를 막아둔다.
	private ValidationUtil() {
	}

	/**
	 * 게시글 제목 검사.
	 * 각 조건마다 에러 메시지 출력을 따로 정의해준다 (입력검증, 길이 검증)
	 */
	public static void validateArticleTitle(String title) {
		// 공백만 입력한 경우를 0글자로 처리하기 위해 trim() 후 길이를 잰다.
		String trimmedTitle = title.trim();

		if (trimmedTitle.length() == 0) {
			throw new ArticleException("게시글 제목은 반드시 입력해야 합니다.");
		}

		if (trimmedTitle.length() > MAX_TITLE_LENGTH) {
			throw new ArticleException("게시글 제목은 " + MAX_TITLE_LENGTH + "글자 이내로 작성해야 합니다.");
		}
	}

	/**
	 * 게시글 작성자 이름 검사.
	 */
	public static void validateArticleWriter(String writer) {
		if (writer.trim().length() == 0) {
			throw new ArticleWriterException("작성자 이름은 반드시 입력해야 합니다.");
		}
	}

}
