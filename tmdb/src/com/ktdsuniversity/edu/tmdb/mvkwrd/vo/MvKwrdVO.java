package com.ktdsuniversity.edu.tmdb.mvkwrd.vo;

public class MvKwrdVO {

	private String mvKwrdId;
	private String kwrdId;
	private String mvId;

	public String getMvKwrdId() {
		return mvKwrdId;
	}

	public void setMvKwrdId(String mvKwrdId) {
		this.mvKwrdId = mvKwrdId;
	}

	public String getKwrdId() {
		return kwrdId;
	}

	public void setKwrdId(String kwrdId) {
		this.kwrdId = kwrdId;
	}

	public String getMvId() {
		return mvId;
	}

	public void setMvId(String mvId) {
		this.mvId = mvId;
	}

	@Override
	public String toString() {
		return "MvKwrdVO [mvKwrdId=" + mvKwrdId + ", kwrdId=" + kwrdId + ", mvId=" + mvId + "]";
	}

}
