package com.banking.model;

public class AuditLog {
	private int targetId;
	private AuditlogActions auditlogActions;
	private long createdTime;
	private int userId;
	private String description;

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public AuditlogActions getAuditlogActions() {
		return auditlogActions;
	}

	public void setAuditlogActions(int actionId) {
		AuditlogActions auditlogActions = AuditlogActions.fromValue(actionId);
		this.auditlogActions = auditlogActions;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
