package json.client.login;

import json.client.ClientJSON;

public class SuccessLogin implements ClientJSON {

	private String state;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}
