package board;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시판의 게시글. 게시글은 댓글 목록을 가진다. (Article has a List&lt;Comment&gt;)
 */
public class Article extends BoardItem {

	/** 게시글 하나에 등록할 수 있는 댓글의 최대 개수 */
	public static final int MAX_COMMENT_COUNT = 10;

	private String title;
	private int hit;
	private final List<Comment> comments;

	public Article(String title, String contents, String writer, String date) {
		super(contents, writer, date);
		this.title = title;
		this.hit = 0; // 조회 수는 항상 0에서 시작한다.
		this.comments = new ArrayList<>(); // 밖에서 받지 않고 직접 만들어 null이 될 수 없게 한다.
	}

	

	public boolean isCommentFull() {
		return comments.size() >= MAX_COMMENT_COUNT;
	}

	public void addComment(Comment comment) {
		comments.add(comment);
	}

	public void removeComment(int listIndex) {
		comments.remove(listIndex);
	}

	public void clearComments() {
		comments.clear();
	}

}
