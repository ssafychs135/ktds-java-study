package com.ktdsuniversity.edu.tmdb.bgrnd.vo;

public class BgrndVO {

	private String bkgrdId;
	private String mvId;
	private String bkgrdUrl;

	public String getBkgrdId() {
		return bkgrdId;
	}

	public void setBkgrdId(String bkgrdId) {
		this.bkgrdId = bkgrdId;
	}

	public String getMvId() {
		return mvId;
	}

	public void setMvId(String mvId) {
		this.mvId = mvId;
	}

	public String getBkgrdUrl() {
		return bkgrdUrl;
	}

	public void setBkgrdUrl(String bkgrdUrl) {
		this.bkgrdUrl = bkgrdUrl;
	}

	@Override
	public String toString() {
		return "BgrndVO [bkgrdId=" + bkgrdId + ", mvId=" + mvId + ", bkgrdUrl=" + bkgrdUrl + "]";
	}

}
