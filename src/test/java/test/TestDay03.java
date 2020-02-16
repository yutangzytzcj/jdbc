package test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import dao.DeptDao;
import entity.Dept;
import util.DBUtil;

public class TestDay03 {

	/**
	 * 批量添加员工,共108个员工,
	 * 每批添加50个.
	 */
	@Test
	public void test1() {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);
			
			String sql = 
				"insert into emps  values("
				+ "emps_seq.nextval,"
				+ "?,?,?,?,?,?,?)";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			for(int i=0;i<108;i++) {
				//模拟一份数据
				ps.setString(1, "好汉"+i);
				ps.setString(2, "打劫");
				ps.setInt(3, 10);
				ps.setDate(4, Date.valueOf("2017-04-11"));
				ps.setDouble(5, 5000.0);
				ps.setDouble(6, 6000.0);
				ps.setInt(7, 9);
				//将数据暂存到ps内
				ps.addBatch();
				//每50条数据批量提交一次
				if(i%50==0) {
					ps.executeBatch();
					//清空暂存数据,以便于下一轮批量提交
					ps.clearBatch();
				}
			}
			
			//为了避免有多余的零头,单独提交一次.
			//因为这是最后一次提交,不必clear了.
			ps.executeBatch();
			
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException(
					"回滚失败", e);
			}
			e.printStackTrace();
			throw new RuntimeException(
				"批量添加员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 获取自动生成的ID:
	 * 1.先增加一个部门
	 * 2.再给这个部门增加一个员工
	 * 3.增加员工时需要获取部门ID
	 */
	@Test
	public void test2() {
		//假设页面传入的部门数据是:
		String dname = "售后保障部";
		String loc = "北京";
		//假设页面传入的员工数据是:
		String ename = "刘备";
		String job = "经理";
		int mgr = 0;
		Date date = Date.valueOf("2017-04-11");
		double sal = 8000.0;
		double comm = 3000.0;
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
	 	conn.setAutoCommit(false);
			
			//1.增加一个部门
			String sql = 
				"insert into depts "
				+ "values(depts_seq.nextval,?,?)";
			//参数2用来告诉ps,让它记住哪些字段的值.
			//在执行insert后,ps会将这些字段的值返回.
			PreparedStatement ps = 
				conn.prepareStatement(
					sql, new String[]{"deptno"});

			ps.setString(1, dname);
			ps.setString(2, loc);
			ps.executeUpdate();
			//2.获取新增部门的ID
			//rs中存的是让ps所记录的字段的值
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			int deptno =20;
					//rs.getInt("deptno");
			//3.增加一个员工
			String sql2 = 
				"insert into emps values("
				+ "emps_seq.nextval,"
				+ "?,?,?,?,?,?,?)";
			PreparedStatement ps2 = 
				conn.prepareStatement(sql2);
			ps2.setString(1, ename);		
			ps2.setString(2, job);	
			ps2.setInt(3, mgr);
			ps2.setDate(4, date);
			ps2.setDouble(5, sal);
			ps2.setDouble(6, comm);
			ps2.setInt(7, deptno);
			ps2.executeUpdate();
			
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
				throw new RuntimeException(
					"回滚失败", e);
			}
			e.printStackTrace();
			throw new RuntimeException(
				"增加部门和员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 分页查询员工数据
	 */
	@Test
	public void test3() {
		//假设需求规定每页显示的行数如下
		int size = 10;
		//假设用户在页面上点击了第2页
		int page = 2;
		
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from ("
				+ "	select e.*,rownum r from ("
				+ "		select * from emps "
				+ "		order by empno"
				+ "	) e"
				+ ") where r between ? and ?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			//起始行
			ps.setInt(1, (page-1)*size+1);
			//终止行
			ps.setInt(2, page*size);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				System.out.println(
					rs.getInt("empno") + "," +
						rs.getString("ename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"分页查询员工失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * 测试:
	 * DeptDao.findById()
	 * DeptDao.update()
	 */
	@Test
	public void test4() {
		DeptDao dao = new DeptDao();
		Dept d = dao.findById(1);
		System.out.println(d.getDname());
		d.setDname("测试部");
		dao.update(d);
	}
	
	/**
	 * DeptDao.findAll()
	 */
	@Test
	public void test5() {
		DeptDao dao = new DeptDao();
		List<Dept> list = dao.findAll();
		for(Dept d : list) {
			System.out.println(d.getDeptno());
		}
	}
	
}












