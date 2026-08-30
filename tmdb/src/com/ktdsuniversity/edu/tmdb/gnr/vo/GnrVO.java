package com.ktdsuniversity.edu.tmdb.gnr.vo;

public class GnrVO {

	private String gnrId;
	private String gnrNm;

	public String getGnrId() {
		return gnrId;
	}

	public void setGnrId(String gnrId) {
		this.gnrId = gnrId;
	}

	public String getGnrNm() {
		return gnrNm;
	}

	public void setGnrNm(String gnrNm) {
		this.gnrNm = gnrNm;
	}

	@Override
	public String toString() {
		return "GnrVO [gnrId=" + gnrId + ", gnrNm=" + gnrNm + "]";
	}

}
