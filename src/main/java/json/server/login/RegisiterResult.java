package json.server.login;

import json.server.ServerJSON;

public class RegisiterResult implements ServerJSON{

	private boolean success;
	private String reason;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

}
