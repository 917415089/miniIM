package pre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import server.db.StatementManager;
import server.session.ChannelManager;

public class Test {
	public static void main(String[] args) throws SQLException {
		Statement statement = StatementManager.getStatement();
		String username = "user8";
		String userpassword = "123";
		String useremail = "useremail@123.com";
		String sql = "insert into user (username,userpassword,useremail) values (\""+username+"\",\""+userpassword+"\",\""+useremail+"\");";
		System.out.println(sql);
		int resultSet = statement.executeUpdate(sql);
		
		
	}
}
