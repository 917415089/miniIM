package json.client.access;

import java.util.List;

import json.client.ClientJSON;

public class SupportedAlgorithm implements ClientJSON{

	private List<String> supSysKey;
	private List<String> supPubKey;
	
	public List<String> getSupSysKey() {
		return supSysKey;
	}
	public void setSupSysKey(List<String> supSysKey) {
		this.supSysKey = supSysKey;
	}
	public List<String> getSupPubKey() {
		return supPubKey;
	}
	public void setSupPubKey(List<String> supPubKey) {
		this.supPubKey = supPubKey;
	}
	
	
}
