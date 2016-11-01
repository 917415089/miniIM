package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SessionTool {
	
	static public String GenerateSID(String name,String password){
		String str= name+password+System.currentTimeMillis();
		MessageDigest instance;
		
		try {
			instance = MessageDigest.getInstance("SHA-1");
			instance.update(str.getBytes());
			byte[] md = instance.digest();
			StringBuffer stringBuffer = new StringBuffer();

			for(int i = 0 ; i < md.length; i++){
				String shahex = Integer.toHexString(md[i]);
				if(shahex.length()<2){
					stringBuffer.append(0);
				}
				stringBuffer.append(shahex);
			}
		return stringBuffer.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
