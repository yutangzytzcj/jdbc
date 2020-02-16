package util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBTool {
	
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	
	static {
		//类加载时只读取一次参数即可
		Properties p = new Properties();
		try {
			p.load(DBTool.class.getClassLoader()
				.getResourceAsStream("db.properties"));
			driver = p.getProperty("driver");
			url = p.getProperty("url");
			user = p.getProperty("user");
			pwd = p.getProperty("pwd");
			//只需要注册一次驱动即可
			Class.forName(driver);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"加载db.properties失败", e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"找不到这个驱动类", e);
		}
	}
	
	/**
	 * 创建连接
	 * 抛出异常,希望调用者不要忘记catch,
	 * 从而希望他别忘记写finally以及关闭连接.
	 */
	public Connection getConnection() 
		throws SQLException {
		return DriverManager
			.getConnection(url, user, pwd);
	}
	
	/**
	 * 关闭连接
	 */
	public void close(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(
					"关闭连接失败", e);
			}
		}
	}
}














