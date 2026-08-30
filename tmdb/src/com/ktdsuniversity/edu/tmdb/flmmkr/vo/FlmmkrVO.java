package com.ktdsuniversity.edu.tmdb.flmmkr.vo;

public class FlmmkrVO {

	private String flmmkrId;
	private String invlvdId;
	private String mvId;
	private String role;
	private String part;

	public String getFlmmkrId() {
		return flmmkrId;
	}

	public void setFlmmkrId(String flmmkrId) {
		this.flmmkrId = flmmkrId;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	@Override
	public String toString() {
		return "FlmmkrVO [flmmkrId=" + flmmkrId + ", invlvdId=" + invlvdId + ", mvId=" + mvId + ", role=" + role
				+ ", part=" + part + "]";
	}

}
