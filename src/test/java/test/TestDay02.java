package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import util.DBUtil;

public class TestDay02 {
	
	/**
	 * 测试DBUtil
	 */
	@Test
	public void test1() {
		//假设浏览器传入了如下查询条件:
		int empno = 3;
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			System.out.println(conn);
			Statement smt = conn.createStatement();
			String sql = 
				"select * from emps "
				+ "where empno>" + empno;
			ResultSet rs = smt.executeQuery(sql);
			while(rs.next()) {
				System.out.println(rs.getInt("empno"));
				System.out.println(rs.getString("ename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"创建连接失败",e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 使用PS执行DML语句
	 */
	@Test
	public void test2() {
		//假设页面传入的修改数据如下:
		int empno = 1;
		String ename = "火云邪神";
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"update emps set ename=? "
				+ "where empno=?";
			//创建PS,发送SQL,编译SQL
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			//给?赋值,将此值存入PS
			//ps.set类型(?的序号,?的值)
			ps.setString(1, ename);
			ps.setInt(2, empno);
			//发送参数,执行计划
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"修改员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 使用PS执行DQL语句
	 */
	@Test
	public void test3() {
		//假设页面传入的查询条件如下:
		int deptno = 1;
		double sal = 5000.0;
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from emps "
				+ "where deptno=? and sal>?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ps.setInt(1, deptno);
			ps.setDouble(2, sal);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				System.out.println(rs.getInt("empno"));
				System.out.println(rs.getString("ename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"查询员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * PS执行查询语句时可以避免注入攻击
	 */
	@Test
	public void test4() {
		//假设网页传入的账号密码如下:
		String username = "zhangsan";
		String password = "a' or 'b'='b";
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from users "
				+ "where username=? "
				+ "and password=?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				System.out.println("登录成功");
			} else {
				System.out.println("账号或密码错误");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"检查账号密码失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}

	/**
	 * 观察ResultSetMetaData中包含什么样的数据
	 */
	@Test
	public void test5() {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from emps";
			Statement smt = conn.createStatement();
			ResultSet rs = smt.executeQuery(sql);
			//获取结果集元数据
			ResultSetMetaData md = rs.getMetaData();
			System.out.println(md.getColumnCount());
			System.out.println(md.getColumnName(1));
			System.out.println(md.getColumnTypeName(1));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"查询员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 模拟实现转账业务:
	 * 
	 * 前提:
	 * 	用户已经登录了网银,开始转账.
	 * 	他已经输入了转账金额和收款账户.
	 * 
	 * 1.查询付款账户余额,看够不够
	 * 2.查询收款账户,判断是否正确
	 * 3.修改付款账户,将余额-N元
	 * 4.修改收款账户,将余额+N元
	 * 
	 */
	@Test
	public void test6() {
		//假设用户输入了如下数据:
		//登录的网银账号(付款账号)
		String payId = "00001";
		//收款账号
		String recId = "00002";
		//转账金额
		double mny = 1000.0;
		
		//为了保障整个业务处于一个事务内,
		//这4次数据库访问共用一个连接.
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			//取消自动提交
			conn.setAutoCommit(false);
			//1.查询付款账户余额,看够不够
			String sql = 
				"select * from accounts "
				+ "where id=?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ps.setString(1, payId);
			ResultSet rs = ps.executeQuery();
			double payMoney = 0.0;
			if(rs.next()) {
				payMoney = rs.getDouble("money");
				if(payMoney<mny) {
					System.out.println("余额不足");
					return;
				}
			}
			//2.查询收款账户,判断是否正确
			String sql2 = 
				"select * from accounts "
				+ "where id=?";
			PreparedStatement ps2 = 
				conn.prepareStatement(sql2);
			ps2.setString(1, recId);
			ResultSet rs2 = ps2.executeQuery();
			double recMoney = 0.0;
			if(!rs2.next()) {
				System.out.println("收款账号不存在");
				return;
			} else {
				recMoney = rs2.getDouble("money");
			}
			//3.修改付款账户,将余额-N元
			String sql3 = 
				"update accounts set "
				+ "money=? where id=?";
			PreparedStatement ps3 = 
				conn.prepareStatement(sql3);
			ps3.setDouble(1, payMoney-mny);
			ps3.setString(2, payId);
			ps3.executeUpdate();
			
			Integer.valueOf("断网了");
			
			//4.修改收款账户,将余额+N元
			String sql4 = 
				"update accounts  set "
				+ "money=? where id=?";
			PreparedStatement ps4 = 
				conn.prepareStatement(sql4);
			ps4.setDouble(1, recMoney+mny);
			ps4.setString(2, recId);
			ps4.executeUpdate();
			//一个完整的转账业务,只需提交一次事务
			conn.commit();
		} catch (Exception e) {
			//回滚事务
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException(
					"回滚事务失败", e);
			}
			e.printStackTrace();
			throw new RuntimeException(
				"转账失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
}















