package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerDAOImpl implements ServerDAO {

	private final static String dbName = "jdbc:sqlite:CloudClassRoom.db";
	private final static int timeout = 10;
	private final Connection conn;

	public ServerDAOImpl() {
		try {
			java.lang.Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(dbName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void execSqlCmd(final String cmd) {
		try {
			Statement stat = null;
			stat = conn.createStatement();
			stat.setQueryTimeout(timeout);
			stat.executeUpdate(cmd);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ResultSet execSqlQuery(final String qry) {
		try {
			Statement stat = null;
			stat = conn.createStatement();
			stat.setQueryTimeout(timeout);
			return stat.executeQuery(qry);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<User> loadUsers() {
		final List<User> users = new ArrayList<User>();
		final ResultSet rs = execSqlQuery("SELECT * FROM user");
		try {
			while (rs.next()) {
				final String user = rs.getString("name");
				final String password = rs.getString("pass");
				final String role = rs.getString("role");
				if ("S".equals(role)) {
					users.add(new Student(user, password));
				} else if ("I".equals(role)) {
					users.add(new Instructor(user, password));
				} else {
					throw new RuntimeException("unknown user role " + role);
				}
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		return users;
	}

	@Override
	public Map<Long, Class> loadClasses() {
		final Map<Long, Class> classes = new HashMap<Long, Class>();
		final ResultSet rs = execSqlQuery("SELECT c.id, c.name, c.instructor, c.presenter, u.pass FROM Class c, User u WHERE u.name=c.instructor");
		try {
			while (rs.next()) {
				final long classId = rs.getLong("id");
				final String className = rs.getString("name");
				final Instructor instructor = new Instructor(
						rs.getString("instructor"), rs.getString("pass"));
				final ResultSet students = execSqlQuery("SELECT u.name, u.pass FROM user u, Class_Student c WHERE u.name=c.student AND class_id = "
						+ classId);
				final Map<String, Student> stud = new HashMap<String, Student>();
				while (students.next()) {
					final String user = students.getString("name");
					final String password = students.getString("pass");
					stud.put(user, new Student(user, password));
				}
				final Class c = new Class(classId, className, instructor, stud);
				c.assignPresenter(rs.getString("presenter"));
				classes.put(classId, c);
			}
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		return classes;
	}

	@Override
	public void insertNewClass(final Class c) {
		execSqlCmd("INSERT INTO Class (id, name, instructor, presenter) VALUES ("
				+ c.getClassId()
				+ ",'"
				+ c.getClassName()
				+ "','"
				+ c.getInstructor().getUserName()
				+ "','"
				+ c.getPresenter().getUserName() + "')");
	}

	@Override
	public void deleteClass(final long classId) {
		execSqlCmd("DELETE FROM Class_Student WHERE class_id= " + classId);
		execSqlCmd("DELETE FROM Class WHERE id = " + classId);
	}

	@Override
	public void addStudentToClass(final long classId, final String student) {
		execSqlCmd("INSERT INTO Class_Student (class_id, student) VALUES ("
				+ classId + ",'" + student + "')");
	}

	@Override
	public void leaveClass(final long classId, final String student) {
		execSqlCmd("DELETE FROM Class_Student WHERE class_id= " + classId
				+ " AND student= '" + student + "'");
	}

	@Override
	public void assignPresenter(final long classId, final String presenter) {
		execSqlCmd("UPDATE Class SET presenter= '" + presenter + "' where id= "
				+ classId);
	}

	@Override
	public void insertUser(final User user) {
		String role;
		if (user instanceof Student){
			role = "S";
		}else{
			role = "I";
		}
		execSqlCmd("INSERT INTO user (name, pass, role) VALUES (" 
				+"'"
				+ user.getUserName()
				+ "','"
				+ user.getPassword()
				+ "','"
				+ role + "')");	
	}
}
