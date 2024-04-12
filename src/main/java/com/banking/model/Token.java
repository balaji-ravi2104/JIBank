package com.banking.model;

public class Token {
	private int tokenId;
	private int userId;
	private String token;
	private long createdTime;
	private long validUpto;
	private TokenStatus tokenStatus;

	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(long validUpto) {
		this.validUpto = validUpto;
	}

	public TokenStatus getTokenStatus() {
		return tokenStatus;
	}

	public void setTokenStatus(int statusId) {
		TokenStatus tokenStatus = TokenStatus.fromValue(statusId);
		this.tokenStatus = tokenStatus;
	}

	@Override
	public String toString() {
		return "Token [tokenId=" + tokenId + ", userId=" + userId + ", token=" + token + ", createdTime=" + createdTime
				+ ", validUpto=" + validUpto + ", tokenStatus=" + tokenStatus + "]";
	}
	
}
