package dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entity.Dept;
import util.DBUtil;

public class DeptDao implements Serializable {

	private static final long serialVersionUID = -3029437005355874537L;

	/**
	 * insert into depts 
	 * values(depts_seq.nextval,?,?)
	 */
	public void save(Dept dept) {
		
	}
	
	public void update(Dept dept) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"update depts set "
				+ "dname=?,loc=? where deptno=?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ps.setString(1, dept.getDname());
			ps.setString(2, dept.getLoc());
			ps.setInt(3, dept.getDeptno());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"修改部门失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
	/**
	 * delete from depts where deptno=?
	 */
	public void delete(int id) {
		
	}
	
	public Dept findById(int id) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from depts "
				+ "where deptno=?";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Dept d = new Dept();
				d.setDeptno(rs.getInt("deptno"));
				d.setDname(rs.getString("dname"));
				d.setLoc(rs.getString("loc"));
				return d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"查询部门失败", e);
		} finally {
			DBUtil.close(conn);
		}
		return null;
	}
	
	public List<Dept> findAll() {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = 
				"select * from depts";
			PreparedStatement ps = 
				conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<Dept> list = new ArrayList<Dept>();
			while(rs.next()) {
				Dept d = new Dept();
				d.setDeptno(rs.getInt("deptno"));
				d.setDname(rs.getString("dname"));
				d.setLoc(rs.getString("loc"));
				list.add(d);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"查询部门失败", e);
		} finally {
			DBUtil.close(conn);
		}
	}
	
}








