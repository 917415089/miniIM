package util;

public class VerifyLogin {

	public static boolean verifyNameandPassword(String name, String password){
		if(verifyName(name)&&verifyPassword(name,password))
			return true;
		return false;
	}

	public static boolean verifyPassword(String name, String password) {
		//test
		if(password.equals("123")&&verifyName(name))
			return true;
		return false;
	}

	public static boolean verifyName(String name) {
		if(name.equals("user1"))
			return true;
		return false;
	}

	
}
