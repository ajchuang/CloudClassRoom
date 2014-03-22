import java.util.*;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// main class for data structure storage
public class Server_DataRepo {
    
    // const
    final static String fm_dbName = "jdbc:sqlite:CloudClassRoom.db";
    final static int fm_timeout = 10;
    
    // main data structures
    Connection m_dbConn;

    // static data 
    static Server_DataRepo m_repo = null; 

    private Server_DataRepo () {
        
        connectDb ();
        initDbTable ();
    }
    
    public static Server_DataRepo getDataRepo () {
        if (m_repo == null)
            m_repo = new Server_DataRepo ();
            
        return m_repo;
    }
    
    void connectDb () {
        
        try {
            Class.forName ("org.sqlite.JDBC");
            m_dbConn = DriverManager.getConnection (fm_dbName);
        } catch (Exception e) {
            Server.logErr ("connect DB exception: " + e);
            e.printStackTrace ();
            System.exit (0);
        }
    }
    
    void initDbTable () {
        
        try {
            execSqlCmd ("create table if not exists ValidUsers (name string, pass string); ");
            execSqlCmd ("create table if not exists LoginUsers (name string, session_id integer); ");
            execSqlCmd ("create table if not exists AllClasses (class_id intetger, class_name string, session_id integer, inst_name string); ");
            
        } catch (Exception e) {
            Server.logErr ("init DB exception: " + e);
            e.printStackTrace ();
            System.exit (0);
        }
    }
    
    void execSqlCmd (String cmd) throws Exception {
        Statement stat = null;
        stat = m_dbConn.createStatement ();
        stat.setQueryTimeout (fm_timeout);  // set timeout to 30 sec.
        stat.executeUpdate (cmd);
    }
    
    ResultSet execSqlQuery (String qry) throws Exception {
        Statement stat = null;
        stat = m_dbConn.createStatement ();
        stat.setQueryTimeout (fm_timeout);  // set timeout to 30 sec.
        return stat.executeQuery (qry);
    }
    
    public boolean isValidUser (String name, String pass) {
        
        String sql = "select count (*) as RECORDCOUNT from ValidUsers where name = '" + name + "' AND pass = '" + pass + "';";
        
        try {
            ResultSet rs = execSqlQuery (sql);
            
            if (rs.next () && rs.getInt ("RECORDCOUNT") == 1)
                return true;
            else
                return false;
        } catch (Exception e) {
            Server.logErr ("Exception @ isValidUser:" + e);
            e.printStackTrace ();
            return false;
        }
    }
}