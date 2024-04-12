package com.banking.model;

public class AuditLog {
	private int targetId;
	private AuditlogActions auditlogActions;
	private long createdTime;
	private int userId;
	private String description;
	private Status status;
	
	public AuditLog(int targetId, AuditlogActions auditlogActions, long createdTime, int userId, String description,Status status) {
		super();
		this.targetId = targetId;
		this.auditlogActions = auditlogActions;
		this.createdTime = createdTime;
		this.userId = userId;
		this.description = description;
		this.status = status;
	}

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
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(int statusId) {
		Status status = Status.fromValue(statusId);
		this.status = status;
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

	@Override
	public String toString() {
		return "AuditLog [targetId=" + targetId + ", auditlogActions=" + auditlogActions + ", createdTime="
				+ createdTime + ", userId=" + userId + ", description=" + description + "]";
	}
	
}
