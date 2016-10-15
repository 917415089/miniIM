package json.client;

import java.util.List;

public class SupportedAlgorithm implements ClientJSON{
	private String process;
	private List<String> supSysKey;
	private List<String> supPubKey;
	
	
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
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
