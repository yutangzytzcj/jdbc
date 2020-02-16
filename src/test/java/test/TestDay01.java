package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Test;

import util.DBTool;

/**
 * Junit测试类：
 * 1.每个方法都可以单独执行
 * 2.需要在方法前加@Test
 * 3.需要单独导包,但无需使用maven,
 *   因为在正式环境下,测试代码以及
 *   junit的包都可以被抛弃.
 * 4.方法必须是公有的,无返回值,无参数
 */
public class TestDay01 {

	/**
	 * 演示如何使用jdbc创建连接
	 */
	@Test
	public void test1() {
		Connection conn = null;
		try {
			//注册驱动
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//创建连接
			conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521:xe", 
				"system", "123456");
			System.out.println(conn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到驱动",e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("创建连接失败",e);
		} finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new RuntimeException(
						"关闭连接失败",e);
				}
			}
		}
		
	}
	
	/**
	 * 演示如何使用Properties读取db.properties
	 * 1.Properties类专门用来读取properties文件
	 * 2.Properties类本质上就是Map
	 */
	@Test
	public void test2() {
		Properties p = new Properties();
		try {
			//从类路径(classes)下加载db.properties,
			//加载后p中就有了该文件中的所有数据.
			p.load(TestDay01.class.getClassLoader()
				.getResourceAsStream("db.properties"));
			//从p中获取数据
			System.out.println(p.getProperty("driver"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 1.演示使用DBTool创建连接
	 * 2.如何执行DML语句
	 */
	@Test
	public void test3() {
		DBTool tool = new DBTool();
		Connection conn = null;
		try {
			conn = tool.getConnection();
			System.out.println(conn);
			Statement smt = conn.createStatement();
			//jdbc中的sql结尾处不能写;
			String sql = 
				"insert into emps values("
				+ "emps_seq .nextval,"
				+ "'唐僧',0,'领导',sysdate,"
				+ "6000.0,8000.0,3)";
			//执行DML
			int rows = smt.executeUpdate(sql);
			System.out.println(rows);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			tool.close(conn);
		}
	}
	
	/**
	 * 演示如何执行DQL语句
	 */
	@Test
	public void test4() {
		DBTool tool = new DBTool();
		Connection conn = null;
		try {
			conn = tool.getConnection();
			Statement smt = conn.createStatement();
			String sql = 
				"select * from emps  order by empno";
			//执行查询语句,返回结果集.
			//ResultSet是采用迭代器模式设计的,
			//迭代器通常都是使用while进行遍历.
			ResultSet rs = smt.executeQuery(sql);
			while(rs.next()) {
				//每次循环,我们可以获取一行数据
				//rs.get类型(字段的序号)
				//rs.get类型(字段的名称)
				System.out.println(rs.getInt("empno"));
				System.out.println(rs.getString("ename"));
				System.out.println(rs.getDouble("salary"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"查询员工失败", e);
		} finally {
			tool.close(conn);
		}
	}
	
}














