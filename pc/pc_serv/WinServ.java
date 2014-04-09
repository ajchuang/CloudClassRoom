
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
            boolean isWaiting = false;
        
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
    
    boolean uploadFileToS3 (String fName, String remoteName) {
        try {
            WinServ_CloudHelper.uploadFile (fName, remoteName);
        } catch (Exception e) {
            e.printStackTrace ();
            return false;
        }
        
        return true;
    }
    
    void notifyCloud () {
    }
    
    void processMsg_UpdateFile (WinServ_ReqCommand cmd) {
        // updating a file
        String path = cmd.getStrAt (1);
        WinServ.logInfo ("UPDATE_FILE: " + path);
        
        // TODO: update to the Amazon server. Fix the remote name
        uploadFileToS3 (path, "remote");
        
        // send notification to the server
        notifyCloud ();
    }
    
    void processMsg (WinServ_ReqCommand cmd) {
        // do the job - send info to the real server
        
        String msgType = cmd.getStrAt (0);
        
        if (msgType.equals ("BEGIN UPDATE_FILE")) {
            processMsg_UpdateFile (cmd);
        }
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
    
    public static int getPort () {
        return DEFAULT_PORT;
    }
    
    public static void main (String args[]) throws Exception {
        
        int port = DEFAULT_PORT;
        
        // starting server notification server.
        Thread ntf = new Thread (new WinServ_NotificationListener ());
        ntf.start ();
        
        // starting local working server
        Thread serv = new Thread (new WinServ (port));
        serv.start ();
        
        // starting login UI
        WinServ_LoginWindow loginWin = new WinServ_LoginWindow ();
        
        return;
    }
    
}