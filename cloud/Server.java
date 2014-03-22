
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
    
    Server_ClntMsg readMsg () {
        
        String data;
        Server_ClntMsg clntMsg = new Server_ClntMsg ();
    
        try {    
            while ((data = m_reader.readLine ()) != null) {
                
                data.trim ();
                
                if (data.equals ("END") == true) 
                    break;
                                    
                clntMsg.pushMsg (data);
            }
        } catch (Exception e) {
            Server.log ("Exception: " + e);
            e.printStackTrace ();
        }
        
        return clntMsg;
    }
    
    void sendToProcThread (Server_ClntMsg clntMsg) {
        
        Server.log ("sendToProcThread");
        
        Server_ProcThread.enqueueCmd (clntMsg);
        
        // respond to the user
        m_writer.println ("OK");
        m_writer.flush ();
    }
    
    void processMsg (Socket sc) {
        
        try {
            m_writer = new PrintWriter (sc.getOutputStream (), true);
            m_reader = new BufferedReader (new InputStreamReader (sc.getInputStream ()));
            
            sendToProcThread (readMsg ());
            
            Server.log ("Complete client");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
    
    public void startServer () {
        
        Server_ProcThread pThd = Server_ProcThread.getProcThread ();
        (new Thread (pThd)).start ();
        
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
        
        // 1. init database
        Server_DataRepo.getDataRepo ();
        
        // 2. start welcoming socket
        Server svr = Server.createServer (port);
        svr.startServer ();
    }
}
