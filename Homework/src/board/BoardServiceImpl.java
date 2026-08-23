package board;

import board.utils.ScannerUtil;
import board.utils.ValidationUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 개시판 기능의 세부 구현
 */
public class BoardServiceImpl implements BoardService {

	private static final String LINE = "--------------------------------------------------";

	private final List<Article> articleList = new ArrayList<>();

	// 1. 게시글 작성하기
	@Override
	public void writeArticle() {
		String title = ScannerUtil.nextLine("게시글 제목 : ");
		ValidationUtil.validateArticleTitle(title);

		String writer = ScannerUtil.nextLine("작성자 이름 : ");
		ValidationUtil.validateArticleWriter(writer);

		String date = ScannerUtil.nextLine("작성 날짜   : ");
		String contents = ScannerUtil.nextLine("게시글 내용 : ");

		articleList.add(new Article(title.trim(), contents, writer.trim(), date));

		System.out.println("게시글 작성이 완료되었습니다.");
	}

	// 2. 모든 게시글 출력하기
	@Override
	public void printAllArticles() {
		if (articleList.isEmpty()) {
			System.out.println("아직 등록된 게시글이 없습니다.");
			return;
		}

		for (int i = 0; i < articleList.size(); i++) {
			Article article = articleList.get(i);
			printArticleSummary(i, article);
		}
	}

	// 3. 게시글 번호로 게시글 정보 출력하기
	@Override
	public void printArticle(int articleNo) {
		Article article = findArticle(articleNo);

		// 해당 개시물이 있을 경우에만 코드 진행, null 이면 바로 출력 종료
		if (article == null) {
			return;
		}

		// 게시글을 열어본 것이므로 조회 수를 먼저 올린다.
		article.increaseHit();

		System.out.println(LINE);
		System.out.println("게시글 번호 : " + articleNo);
		System.out.println("제목        : " + article.getTitle());
		System.out.println("작성자      : " + article.getWriter());
		System.out.println("작성 날짜   : " + article.getDate());
		System.out.println("조회 수     : " + article.getHit());
		System.out.println(LINE);
		System.out.println(article.getContents());
		System.out.println(LINE);
		System.out.println("[댓글 " + article.getCommentCount() + "개]");
		printComments(article);
		System.out.println(LINE);
	}

	// 4. 게시글 수정하기
	@Override
	public void modifyArticle(int articleNo) {
		Article article = findArticle(articleNo);
		if (article == null) {
			return;
		}

		System.out.println("현재 제목 : " + article.getTitle());
		String title = ScannerUtil.nextLine("수정할 제목 : ");
		ValidationUtil.validateArticleTitle(title);

		System.out.println("현재 내용 : " + article.getContents());
		String contents = ScannerUtil.nextLine("수정할 내용 : ");

		// 제목과 내용만 수정할 수 있다. 작성자, 작성일, 조회 수, 댓글은 건드리지 않는다.
		article.setTitle(title.trim());
		article.setContents(contents);

		System.out.println("게시글 수정이 완료되었습니다.");
	}

	// 5. 게시글 번호로 게시글 삭제하기
	@Override
	public void removeArticle(int articleNo) {
		if (findArticle(articleNo) == null) {
			return;
		}

		articleList.remove(articleNo - 1);

		System.out.println("게시글을 삭제했습니다.");
	}

	// 6. 게시판에 등록된 게시글의 개수 출력하기
	@Override
	public void printArticleCount() {
		if (articleList.isEmpty()) {
			System.out.println("등록된 게시글이 없습니다");
			return;
		}

		System.out.println(articleList.size() + "개의 게시글이 등록되었습니다.");
	}

	// 7. 게시글에 댓글 작성하기
	@Override
	public void writeComment(int articleNo) {
		Article article = findArticle(articleNo);
		if (article == null) {
			return;
		}

		if (article.isCommentFull()) {
			System.out.println("댓글을 더 이상 등록할 수 없습니다.");
			return;
		}

		String contents = ScannerUtil.nextLine("댓글 내용   : ");
		String writer = ScannerUtil.nextLine("작성자 이름 : ");
		String date = ScannerUtil.nextLine("작성 날짜   : ");

		article.addComment(new Comment(contents, writer, date));

		System.out.println("댓글 작성이 완료되었습니다.");
	}

	// 8. 게시글에 등록된 댓글 삭제하기
	@Override
	public void removeComment(int articleNo, int commentNo) {
		Article article = findArticle(articleNo);
		if (article == null) {
			return;
		}

		if (findComment(article, commentNo) == null) {
			return;
		}

		article.removeComment(commentNo - 1);

		System.out.println("댓글을 삭제했습니다.");
	}

	// 9. 게시글에 등록된 댓글 하나 추천하기
	@Override
	public void recommendComment(int articleNo, int commentNo) {
		Article article = findArticle(articleNo);
		if (article == null) {
			return;
		}

		Comment comment = findComment(article, commentNo);
		if (comment == null) {
			return;
		}

		comment.increaseLike();

		System.out.println("댓글을 추천했습니다. (추천 수 : " + comment.getLikeCount() + ")");
	}

	// 10. 게시글 제목으로 검색하기
	@Override
	public void searchByTitle(String keyword) {
		boolean isFound = false;

		for (int i = 0; i < articleList.size(); i++) {
			Article article = articleList.get(i);

			if (article.getTitle().contains(keyword)) {
				printArticleSummary(i, article);
				isFound = true;
			}
		}

		if (!isFound) {
			System.out.println("검색된 게시글이 없습니다.");
		}
	}

	// 11. 게시글 목록 전체 삭제하기
	@Override
	public void removeAllArticles() {
		if (articleList.isEmpty()) {
			System.out.println("제거할 게시글이 없습니다");
			return;
		}

		// 지우고 나면 셀 수 없으므로 개수를 먼저 기억해둔다.
		int removedCount = articleList.size();
		articleList.clear();

		System.out.println(removedCount + "개의 게시글을 삭제했습니다");
	}

	// 12. 원하는 게시글의 모든 댓글 삭제하기
	@Override
	public void removeAllComments(int articleNo) {
		Article article = findArticle(articleNo);
		if (article == null) {
			return;
		}

		int removedCount = article.getCommentCount();
		if (removedCount == 0) {
			System.out.println("등록된 댓글이 없습니다");
			return;
		}

		article.clearComments();

		System.out.println(removedCount + "개의 댓글을 삭제했습니다");
	}

	/**
	 * 글 번호 체크
	 */
	private Article findArticle(int articleNo) {
		if (articleNo < 1 || articleNo > articleList.size()) {
			System.out.println("잘못된 게시글 번호입니다");
			return null;
		}

		return articleList.get(articleNo - 1);
	}

	/**
	 * 댓글 번호 체크
	 */
	private Comment findComment(Article article, int commentNo) {
		if (commentNo < 1 || commentNo > article.getCommentCount()) {
			System.out.println("잘못된 댓글 번호입니다");
			return null;
		}

		return article.getComments().get(commentNo - 1);
	}

	/**
	 * 개시판 글 리스트 출력
	 */
	private void printArticleSummary(int listIndex, Article article) {
		System.out.println((listIndex + 1) + ". " + article.getTitle() + " (" + article.getCommentCount() + ")");
	}

	/** 헤딩 게시글의 댓글 출력 */
	private void printComments(Article article) {
		List<Comment> comments = article.getComments();

		if (comments.isEmpty()) {
			System.out.println("등록된 댓글이 없습니다.");
			return;
		}

		for (int i = 0; i < comments.size(); i++) {
			Comment comment = comments.get(i);
			System.out.println("  " + (i + 1) + ". " + comment.getContents()
					+ "  - " + comment.getWriter()
					+ " / " + comment.getDate()
					+ " / 추천 " + comment.getLikeCount());
		}
	}

}
