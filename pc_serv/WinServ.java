
import java.util.*;
import java.net.*;
import java.io.*;

public class WinServ implements Runnable {
    
    int m_port;
    static final int DEFAULT_PORT = 5566;
    
    public static void logErr (String s) {
        System.out.println ("[Bug] " + s);
    }
    
    public static void logInfo (String s) {
        System.out.println (" [Info] " + s);
    }
    
    public static void logExp (Exception e, boolean isSysDead) {
        System.out.println (" [Exception] " + e);
        e.printStackTrace ();
        
        if (isSysDead)
            System.exit (0);
    }
    
    public WinServ (int port) {
        m_port = port;
    }
    
    WinServ_ReqCommand readMsg (Socket sc) {
        
        try {
            String data;
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            
            PrintWriter writer = new PrintWriter (sc.getOutputStream (), true);
            BufferedReader reader = new BufferedReader (new InputStreamReader (sc.getInputStream ()));
        
            while ((data = reader.readLine ()) != null) {
                
                data.trim ();
                WinServ.logInfo ("Received: " + data);
                
                if (data.equals ("END") == true) 
                    break;
                else
                    cmd.pushStr (data);
            }
            
            return cmd;
            
        } catch (Exception e) {
            WinServ.logExp (e, false);
        }
        
        return null;
    }
    
    void processMsg (WinServ_ReqCommand cmd) {
        // do the job - send info to the real server
        
    }
    
    void readRsp () {
    }
    
    public void run () {
        
        ServerSocket skt = null;
        
        try { 
            skt = new ServerSocket (m_port);
        } catch (Exception e) {
            WinServ.logExp (e, true);
        }
        
        while (true) {
            try {
                Socket sc = skt.accept ();
                WinServ.logInfo (
                    "### a new client from " +
                    sc.getInetAddress().toString() + ":" +
                    Integer.toString (sc.getPort ()) +
                    " is connected ###");
                    
                // process the user connection
                WinServ_ReqCommand cmd = readMsg (sc);
                processMsg (cmd);
                readRsp ();
                
                // close the local link
                sc.close ();

            } catch (Exception e) {
                WinServ.logExp (e, false);
            }
        }
        
    }
    
    public static void main (String args[]) throws Exception {
        
        int port = DEFAULT_PORT;
        
        // argument check
        if (args.length != 1) {
            WinServ.logErr ("Incorrect Argument Count. Using default port");
        } else {
            try {
                port = Integer.parseInt (args[0]);
                WinServ.logInfo ("Using port: " + port);
            } catch (Exception e) {
                WinServ.logExp (e, true);
            }
        }
        
        Thread ntf = new Thread (new WinServ_NotificationListener ());
        ntf.start ();
        
        Thread serv = new Thread (new WinServ (port));
        serv.start ();
        
        return;
    }
    
}