
import java.util.*;
import java.net.*;
import java.io.*;

// WinServ is a local server used to communicate with different apps
public class WinServ implements Runnable {
    
    int m_port;
    
    static final int DEFAULT_LOCAL_PORT = 5566;
    static final int DEFAULT_COMM_PORT  = 4119;
    static final String sfm_fileSysDir = "./fs/";
    static final String sfm_defaultBucket = "CloudClassRoom";
    
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
    
    int isNumber (String s) {
        try {  
            int d = Integer.parseInt (s);
            return d;  
        } catch (NumberFormatException nfe) {  
            return -1;  
        }  
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
                else {
                    String[] toks = data.split (":");
                    int len;
                    
                    if (toks.length == 1) {
                        WinServ.logInfo ("Push: " + data);
                        cmd.pushStr (data);
                    } else if ((len = isNumber (toks[0])) != -1) {
                        WinServ.logInfo ("Push: " + data.substring (toks[0].length () + 1 ));
                        cmd.pushStr (data.substring (toks[0].length () + 1 ));
                    }
                }
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
            WinServ.logExp (e, false);
            return false;
        }
        
        WinServ.logInfo ("Uploading completed");
        return true;
    }
    
    boolean downloadFileFromS3 (String remoteName, String localName) {
        
        try {
            WinServ_CloudHelper.downloadFile (sfm_fileSysDir+localName, sfm_defaultBucket, remoteName);
        } catch (Exception e) {
            WinServ.logExp (e, false);
            return false;
        }
        
        WinServ.logInfo ("Donwload completed");
        return true;
    }
    
    // TODO: notify the server
    void notifyCloud () {
    }
    
    void processMsg_UpdateFile (WinServ_ReqCommand cmd) {
        // updating a file
        String path = cmd.getStrAt (1);
        File f = new File (path);
        
        WinServ.logInfo ("UPDATE_FILE: " + f.getAbsolutePath () + ":" + f.getName ());
        
        // update to the Amazon server. Fix the remote name
        uploadFileToS3 (path, f.getName ());
        
        // send notification to the server
        notifyCloud ();
    }
    
    void processMsg_DLFile (WinServ_ReqCommand cmd) {
        
        String name = cmd.getStrAt (1);
        WinServ.logInfo ("DL_FILE: " + name);
        
        downloadFileFromS3 (name, name);
    }
    
    void processMsg (WinServ_ReqCommand cmd) {
        // do the job - send info to the real server
        
        String msgType = cmd.getStrAt (0);
        WinServ.logInfo ("processMsg: " + msgType);
        
        if (msgType.equals ("UPDATE_FILE")) {
            processMsg_UpdateFile (cmd);
        } else if (msgType.equals ("DL_FILE")) {
            processMsg_DLFile (cmd);
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
        return DEFAULT_LOCAL_PORT;
    }
    
    public static void main (String args[]) throws Exception {
        
        String  remoteHost;
        int     remotePort;
        
        if (args.length == 2) {
            remoteHost = args[0];
            remotePort = Integer.parseInt (args[1]);
            
        } else {
            remoteHost = "localhost";
            remotePort = DEFAULT_COMM_PORT;
        }
        
        // starting data repo
        WinServ_DataRepo.getDataRepo ();
        
        // starting server notification server.
        Thread ntf = new Thread (WinServ_NtfServer.ntfFactory (remoteHost, remotePort));
        ntf.start ();
        
        // starting local working server
        Thread serv = new Thread (new WinServ (DEFAULT_LOCAL_PORT));
        serv.start ();
        
        // starting login UI
        WinServ_LoginWindow loginWin = new WinServ_LoginWindow ();
        
        return;
    }
    
}