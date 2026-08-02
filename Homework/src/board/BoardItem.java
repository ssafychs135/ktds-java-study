package board;

/**
 * 게시글과 댓글이 공통으로 가지는 정보.
 * 그 자체로 만들어질 일이 없으므로 abstract로 선언한다.
 */
public abstract class BoardItem {

	private String contents;
	private String writer;
	private String date;

	public BoardItem(String contents, String writer, String date) {
		this.contents = contents;
		this.writer = writer;
		this.date = date;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getWriter() {
		return writer;
	}

	public String getDate() {
		return date;
	}

}
