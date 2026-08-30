package com.ktdsuniversity.edu.tmdb.vd.vo;

public class VdVO {

	private String vdId;
	private String mvId;
	private String vdUrl;

	public String getVdId() {
		return vdId;
	}

	public void setVdId(String vdId) {
		this.vdId = vdId;
	}

	public String getMvId() {
		return mvId;
	}

	public void setMvId(String mvId) {
		this.mvId = mvId;
	}

	public String getVdUrl() {
		return vdUrl;
	}

	public void setVdUrl(String vdUrl) {
		this.vdUrl = vdUrl;
	}

	@Override
	public String toString() {
		return "VdVO [vdId=" + vdId + ", mvId=" + mvId + ", vdUrl=" + vdUrl + "]";
	}

}
