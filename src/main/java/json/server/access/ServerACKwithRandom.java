package json.server.access;

import json.server.ServerJSON;

public class ServerACKwithRandom implements ServerJSON{

	private int random;

	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}
	
}
