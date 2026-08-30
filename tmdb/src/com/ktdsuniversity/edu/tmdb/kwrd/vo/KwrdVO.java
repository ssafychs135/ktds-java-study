package com.ktdsuniversity.edu.tmdb.kwrd.vo;

public class KwrdVO {

	private String kwrdId;
	private String kwrdNm;

	public String getKwrdId() {
		return kwrdId;
	}

	public void setKwrdId(String kwrdId) {
		this.kwrdId = kwrdId;
	}

	public String getKwrdNm() {
		return kwrdNm;
	}

	public void setKwrdNm(String kwrdNm) {
		this.kwrdNm = kwrdNm;
	}

	@Override
	public String toString() {
		return "KwrdVO [kwrdId=" + kwrdId + ", kwrdNm=" + kwrdNm + "]";
	}

}
