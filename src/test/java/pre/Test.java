package pre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.db.StatementManager;
import server.session.ChannelManager;

public class Test {
	public static void main(String[] args){
		Statement statement = StatementManager.getStatement();
		String username = "user8";
		String userpassword = "123";
		String useremail = "useremail@123.com";
		String sql = "insert into user (username,userpassword,useremail) values (\""+username+"\",\""+userpassword+"\",\""+useremail+"\");";
		System.out.println(sql);
		int resultSet;
		try {
			resultSet = statement.executeUpdate(sql);
			System.out.println(resultSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage().substring(0,15));
			if(e.getMessage().substring(0,15).equalsIgnoreCase("Duplicate entry")){
				System.out.println("pass");
			}
		}

		
		
	}
}
