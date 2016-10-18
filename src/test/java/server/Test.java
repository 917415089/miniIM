package server;

import java.sql.ResultSet;
import java.sql.Statement;

import server.db.StatementManager;

public class Test {
	public static void main(String[] args) throws Exception {

		Statement statement = StatementManager.getInstance();
	    String query = "select * from USER;";
	    ResultSet resultSet = statement.executeQuery(query);
	   while(resultSet.next()){
		   String name = resultSet.getString("username");
		   String password = resultSet.getString("userpassword");
		   System.out.println("name : "+name+"   password : "+password);
	   }
	   query = "select username from USER";
	   resultSet = statement.executeQuery(query);
	   while(resultSet.next()){
		   String name = resultSet.getString("username");
		   System.out.println("name : "+name);
	   }
	} 
}
