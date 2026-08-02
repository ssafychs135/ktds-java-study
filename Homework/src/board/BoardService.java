package board;

/**
 * 개시판 계약 목록
 */
public interface BoardService {

	/** 1. 게시글 작성하기 */
	void writeArticle();

	/** 2. 모든 게시글 출력하기 */
	void printAllArticles();

	/** 3. 게시글 번호로 게시글 정보 출력하기 */
	void printArticle(int articleNo);

	/** 4. 게시글 수정하기 */
	void modifyArticle(int articleNo);

	/** 5. 게시글 번호로 게시글 삭제하기 */
	void removeArticle(int articleNo);

	/** 6. 게시판에 등록된 게시글의 개수 출력하기 */
	void printArticleCount();

	/** 7. 게시글에 댓글 작성하기 */
	void writeComment(int articleNo);

	/** 8. 게시글에 등록된 댓글 삭제하기 */
	void removeComment(int articleNo, int commentNo);

	/** 9. 게시글에 등록된 댓글 하나 추천하기 */
	void recommendComment(int articleNo, int commentNo);

	/** 10. 게시글 제목으로 검색하기 */
	void searchByTitle(String keyword);

	/** 11. 게시글 목록 전체 삭제하기 */
	void removeAllArticles();

	/** 12. 원하는 게시글의 모든 댓글 삭제하기 */
	void removeAllComments(int articleNo);

}
