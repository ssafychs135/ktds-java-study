package com.ktdsuniversity.edu.tmdb.mvgnr.vo;

public class MvGnrVO {

	private String mvGnrId;
	private String mvId;
	private String gnrId;

	public String getMvGnrId() {
		return mvGnrId;
	}

	public void setMvGnrId(String mvGnrId) {
		this.mvGnrId = mvGnrId;
	}

	public String getMvId() {
		return mvId;
	}

	public void setMvId(String mvId) {
		this.mvId = mvId;
	}

	public String getGnrId() {
		return gnrId;
	}

	public void setGnrId(String gnrId) {
		this.gnrId = gnrId;
	}

	@Override
	public String toString() {
		return "MvGnrVO [mvGnrId=" + mvGnrId + ", mvId=" + mvId + ", gnrId=" + gnrId + "]";
	}

}
