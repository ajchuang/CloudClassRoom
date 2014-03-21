
import java.util.*;
import java.net.*;
import java.io.*;

public class Server {
    
    static Server   sm_server = null;
    
    int m_port;
    PrintWriter m_writer;
    BufferedReader m_reader;
    
    //--- instance methods
    Server (int port) {
        m_port = port;
    }
    
    void readMsg () {
        
        String data;
    
        try {    
            while ((data = m_reader.readLine ()) != null) {
                
                Server.log (data);
                
                if (data.equals ("END") == true) 
                    break;
            }
        } catch (Exception e) {
            Server.log ("Exception: " + e);
            e.printStackTrace ();
        }
        
        return;
    }
    
    void processMsg () {
        Server.log ("processMsg");
        m_writer.println ("OK");
        m_writer.flush ();
    }
    
    void processMsg (Socket sc) {
        
        try {
            m_writer = new PrintWriter (sc.getOutputStream (), true);
            m_reader = new BufferedReader (new InputStreamReader (sc.getInputStream ()));
            
            readMsg ();
            processMsg ();
            
            Server.log ("Complete client");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public void startServer () {
        
        try {
            ServerSocket skt = new ServerSocket (m_port);
            StringBuffer buf = new StringBuffer ();
            
            // @lfred: always ready to serve the clients
            while (true) {
                try {
                    Socket sc = skt.accept ();
                    Server.log ("Server: incoming link");
                    
                    processMsg (sc);
                    
                    sc.close ();
                } catch (Exception ee) {
                    Server.logErr ("exception: " + ee);
                    ee.printStackTrace ();
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
            Server.logErr ("exception: " + e);
        }
    }
    
    //--- static methods
    public static Server createServer (int port) {
        if (sm_server == null)
            sm_server = new Server (port);
        
        return sm_server;
    }
     
    public static void logErr (String msg) {
        System.out.println ("[Error] " + msg);
    }
    
    public static void log (String msg) {
        System.out.println ("  [Info] " + msg);
    }

    // @lfred: to start the server, you need to java Server [port]
    public static void main (String args[]) {
        
        if (args.length != 1) {
            Server.logErr ("Incorrect Params");
            return;
        }
        
        int port = Integer.parseInt (args[0]);
        Server.log ("Using port: " + port);
        
        Server svr = Server.createServer (port);
        svr.startServer ();
    }
}
