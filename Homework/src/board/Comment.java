package board;

/**
 * 게시글에 달리는 댓글.
 * 공통 정보(내용, 작성자, 작성일)는 BoardItem이 가지고, 추천 수만 따로 가진다.
 */
public class Comment extends BoardItem {

	private int likeCount;

	public Comment(String contents, String writer, String date) {
		super(contents, writer, date);
		this.likeCount = 0;
	}

	public int getLikeCount() {
		return likeCount;
	}

	/**
	 * 추천 수는 1씩 증가만 가능하다.
	 * setLikeCount로 아무 값이나 넣을 수 있게 열어두면 규칙을 지킬 방법이 없어진다.
	 */
	public void increaseLike() {
		this.likeCount++;
	}

}
