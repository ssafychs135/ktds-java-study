package com.ktdsuniversity.edu.tmdb.appr.vo;

public class ApprVO {

	private String apprId;
	private String invlvdId;
	private String mvId;
	private String apprRole;

	public String getApprId() {
		return apprId;
	}

	public void setApprId(String apprId) {
		this.apprId = apprId;
	}

	public String getInvlvdId() {
		return invlvdId;
	}

	public void setInvlvdId(String invlvdId) {
		this.invlvdId = invlvdId;
	}

	public String getMvId() {
		return mvId;
	}

	public void setMvId(String mvId) {
		this.mvId = mvId;
	}

	public String getApprRole() {
		return apprRole;
	}

	public void setApprRole(String apprRole) {
		this.apprRole = apprRole;
	}

	@Override
	public String toString() {
		return "ApprVO [apprId=" + apprId + ", invlvdId=" + invlvdId + ", mvId=" + mvId + ", apprRole=" + apprRole
				+ "]";
	}

}
