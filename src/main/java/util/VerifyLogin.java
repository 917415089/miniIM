package util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import com.alibaba.fastjson.JSON;

import json.server.login.SuccessLogin;
import json.server.session.SendBackJSON;
import server.db.StatementManager;

public class VerifyLogin {

	public static boolean verifyNameandPassword(final String name, final String password){
		
		
		
		
		
		Statement statement = StatementManager.getStatement();
		String sql = "select * from USER where username=\""+name+"\";";
		try {
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

	public static boolean verifyPassword(String name, String password) {
		//test
		Statement statement = StatementManager.getStatement();
		String sql = "select * from USER where username=\""+name+"\";";
		try {
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

	public static boolean verifyName(String name) {
		Statement statement = StatementManager.getStatement();
		String sql = "select * from USER where username=\""+name+"\";";
		try {
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
