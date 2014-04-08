package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SqlLiteConsole {

	final static String fm_dbName = "jdbc:sqlite:CloudClassRoom.db";
	final static int fm_timeout = 10;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection m_dbConn;
		try {
			java.lang.Class.forName("org.sqlite.JDBC");
			m_dbConn = DriverManager.getConnection(fm_dbName);
			// execSqlCmd(m_dbConn,
			// "create table if not exists Class (name string, pass string, role string); ");
			// execSqlCmd(m_dbConn,
			// "create table if not exists Class (id number(19), name string, instructor string); ");
			 execSqlCmd(m_dbConn,
			 "create table if not exists Class_Student (class_id number(19), student string); ");
			//execSqlCmd(m_dbConn,
			//"insert into User (name, pass, role) values ('Li Li','6998','I'); ");
			//execSqlCmd(m_dbConn,
			//"insert into Class (id, name, instructor) values (6998,'Mobile','Li Li'); ");
			/*
			final ResultSet result = execSqlQuery(m_dbConn,
					"SELECT * FROM User");
			while (result.next()) {
				System.out.println(result.getString("name") + " "
						+ result.getString("pass") + " "
						+ result.getString("role"));
			}
			*/
			/*
			final ResultSet result = execSqlQuery(m_dbConn,
					"SELECT * FROM class");
			while (result.next()) {
				System.out.println(result.getLong("id") + " "
						+ result.getString("name") + " "
						+ result.getString("instructor"));
			}
			*/
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void execSqlCmd(Connection m_dbConn, String cmd) throws Exception {
		Statement stat = null;
		stat = m_dbConn.createStatement();
		stat.setQueryTimeout(fm_timeout); // set timeout to 30 sec.
		stat.executeUpdate(cmd);
	}

	static ResultSet execSqlQuery(Connection m_dbConn, String qry)
			throws Exception {
		Statement stat = null;
		stat = m_dbConn.createStatement();
		stat.setQueryTimeout(fm_timeout); // set timeout to 30 sec.
		return stat.executeQuery(qry);
	}

}
