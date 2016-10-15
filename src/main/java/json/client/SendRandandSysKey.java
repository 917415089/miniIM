package json.client;

public class SendRandandSysKey implements ClientJSON {

	private int random;
	private byte[] syskeyend;
	
	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}

	public byte[] getSyskeyend() {
		return syskeyend;
	}

	public void setSyskeyend(byte[] syskeyend) {
		this.syskeyend = syskeyend;
	}
	
	
}
