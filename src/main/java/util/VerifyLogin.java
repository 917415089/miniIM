package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VerifyLogin {

	static final VerifyLogin unique = new VerifyLogin();
	
	private Connection conn;
	private VerifyLogin(){
		String url = "jdbc:mysql://localhost:3306/test" ;    
	    String username = "root" ;   
	    String userpassword = "123456" ;   
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url , username , userpassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized boolean verifyNameandPassword(final String name, final String password){
		
		String sql = "select * from user where username=\""+name+"\";";
		try {
			Statement statement = unique.conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
			if(name.equals(resultSet.getString("username"))
					&&password.equals(resultSet.getString("userpassword")))
				return true;
			else
				return false;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static synchronized boolean verifyPassword(String name, String password) {
		//test

		String sql = "select * from user where username=\""+name+"\";";
		try {
			Statement statement = unique.conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
			if(name.equals(resultSet.getString("username"))
					&&password.equals(resultSet.getString("userpassword")))
				return true;
			else
				return false;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static synchronized boolean verifyName(String name) {
		
		String sql = "select * from user where username=\""+name+"\";";
		try {
			Statement statement = unique.conn.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()){
			if(name.equals(resultSet.getString("username")))
				return true;
			else
				return false;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	
}
