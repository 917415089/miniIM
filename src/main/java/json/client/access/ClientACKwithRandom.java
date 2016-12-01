package json.client.access;

import json.client.ClientJSON;

public class ClientACKwithRandom implements ClientJSON{

	private int random;

	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}
	
}
