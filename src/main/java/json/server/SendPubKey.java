package json.server;

public class SendPubKey {
	private String process;
	private String selPubKey;
	private String selSysKey;
	private byte[] pubKeyEncode;
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getSelPubKey() {
		return selPubKey;
	}
	public void setSelPubKey(String selPubKey) {
		this.selPubKey = selPubKey;
	}
	public String getSelSysKey() {
		return selSysKey;
	}
	public void setSelSysKey(String selSysKey) {
		this.selSysKey = selSysKey;
	}
	public byte[] getPubKeyEncode() {
		return pubKeyEncode;
	}
	public void setPubKeyEncode(byte[] pubKeyEncode) {
		this.pubKeyEncode = pubKeyEncode;
	}
	
}
