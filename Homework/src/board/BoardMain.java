package board;

import board.exceptions.ArticleException;
import board.exceptions.ArticleWriterException;
import board.utils.ScannerUtil;

public class BoardMain {

	public static void main(String[] args) {

		BoardService boardService = new BoardServiceImpl();

		// 터미널 입력을 0 을 입력해서 종료하기 전까지 지속적으로 대기한다.
		while (true) {
			// 개시판 메뉴 출력
			printArticleListScreen(boardService);

			// 입력받은 메뉴 번호
			int menuNo = ScannerUtil.nextInt(">> ");

			try {
				if (menuNo == 0) {
					System.out.println("게시판을 종료합니다.");
					ScannerUtil.close();
					break;
				}

				// 터미널 번호 입력에 따른 메뉴로 라우팅
				runMenu(boardService, menuNo);

			} catch (ArticleException | ArticleWriterException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	/**
	 * 반복의 처음 화면. 게시글 목록과 메뉴를 함께 출력한다.
	 */
	private static void printArticleListScreen(BoardService boardService) {
		System.out.println();
		System.out.println("==================== 게 시 판 ====================");
		boardService.printAllArticles();
		System.out.println("--------------------------------------------------");
		System.out.println(" 1. 게시글 작성        2. 전체 게시글 보기");
		System.out.println(" 3. 게시글 상세 보기   4. 게시글 수정");
		System.out.println(" 5. 게시글 삭제        6. 게시글 개수");
		System.out.println(" 7. 댓글 작성          8. 댓글 삭제");
		System.out.println(" 9. 댓글 추천         10. 게시글 제목 검색");
		System.out.println("11. 게시글 전체 삭제  12. 댓글 전체 삭제");
		System.out.println(" 0. 종료");
		System.out.println("--------------------------------------------------");
	}

	/**
	 * 
	 */
	private static void runMenu(BoardService boardService, int menuNo) {

		if (menuNo == 1) {
			boardService.writeArticle();
		} else if (menuNo == 2) {
			boardService.printAllArticles();
		} else if (menuNo == 3) {
			boardService.printArticle(inputArticleNo());

		} else if (menuNo == 4) {
			boardService.modifyArticle(inputArticleNo());
		} else if (menuNo == 5) {
			boardService.removeArticle(inputArticleNo());
		} else if (menuNo == 6) {
			boardService.printArticleCount();
		} else if (menuNo == 7) {
			boardService.writeComment(inputArticleNo());
		} else if (menuNo == 8) {
			int articleNo = inputArticleNo();
			int commentNo = inputCommentNo();
			boardService.removeComment(articleNo, commentNo);
		} else if (menuNo == 9) {
			int articleNo = inputArticleNo();
			int commentNo = inputCommentNo();
			boardService.recommendComment(articleNo, commentNo);
		} else if (menuNo == 10) {
			boardService.searchByTitle(ScannerUtil.nextLine("검색어 : "));
		} else if (menuNo == 11) {
			boardService.removeAllArticles();
		} else if (menuNo == 12) {
			boardService.removeAllComments(inputArticleNo());

		} else {
			// 숫자가 아닌 값을 입력하면 ScannerUtil이 -1을 돌려주므로 이 경우도 여기서 걸린다.
			System.out.println("잘못된 입력입니다. 게시글 목록으로 돌아갑니다.");
			
		}

	}

	private static int inputArticleNo() {
		return ScannerUtil.nextInt("게시글 번호 : ");
	}

	private static int inputCommentNo() {
		return ScannerUtil.nextInt("댓글 번호 : ");
	}

}
