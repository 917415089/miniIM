package json.server.login;

import json.server.ServerJSON;

public class SuccessLogin implements ServerJSON{
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String str) {
		this.token = str;
	}
	
}
