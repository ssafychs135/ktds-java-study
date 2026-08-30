package com.ktdsuniversity.edu.tmdb.invlvd.vo;

public class InvlvdVO {

	private String invlvdId;
	private String phtUrl;
	private String invlvdNm;

	public String getInvlvdId() {
		return invlvdId;
	}

	public void setInvlvdId(String invlvdId) {
		this.invlvdId = invlvdId;
	}

	public String getPhtUrl() {
		return phtUrl;
	}

	public void setPhtUrl(String phtUrl) {
		this.phtUrl = phtUrl;
	}

	public String getInvlvdNm() {
		return invlvdNm;
	}

	public void setInvlvdNm(String invlvdNm) {
		this.invlvdNm = invlvdNm;
	}

	@Override
	public String toString() {
		return "InvlvdVO [invlvdId=" + invlvdId + ", phtUrl=" + phtUrl + ", invlvdNm=" + invlvdNm + "]";
	}

}
