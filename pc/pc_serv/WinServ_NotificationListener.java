
import java.util.*;
import java.net.*;
import java.io.*;

public class WinServ_NotificationListener implements Runnable {
    
    static int DEFAULT_NFT_SIZE = 1024;
    
    int m_port; 
    String m_addr;
    Socket m_socket;
    PrintWriter m_outputStream;
    BufferedReader m_inputStream;
    
    public WinServ_NotificationListener (String addr, int port) {
        m_addr = addr;
        m_port = port;
    }
    
    public void sendMsgToServer (WinServ_ReqCommand cmd) {
        
    }
    
    public void process_NTF (WinServ_ReqCommand cmd) {
        
        /*
        String toks[] = str.split (" ");
        WinServ.logInfo ("process_NTF: " + toks[0]);
        
        if (toks[0].equals ("DL_FILE")) {
            String fName = toks[1];
            
            try {
                Socket sck = new Socket ("localhost", 5566);
                PrintWriter writer = new PrintWriter (sck.getOutputStream (), true);
                writer.println ("DL_FILE");
                writer.println (Integer.toString (fName.length ()) + ":" + fName);
                writer.println ("END");
                sck.close ();
            } catch (Exception e) {
                WinServ.logExp (e, false);
            }
        }
        */
    }
    
    public void run () {
        
        try {
            m_socket = new Socket (m_addr, m_port);
            m_outputStream = new PrintWriter (m_socket.getOutputStream ());
            m_inputStream = new BufferedReader (new InputStreamReader (m_socket.getInputStream ()));
                        
            while (true) {
                
                String data;
                WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
                
                while ((data = m_inputStream.readLine ()) != null) {
                    data.trim ();
                    WinServ.logInfo ("Received: " + data);
                
                    if (data.equals ("END") == true) 
                        break;
                    else {
                        cmd.pushStr (data);
                    }
                }
                
                process_NTF (cmd);
            }
        } catch (Exception e) {
            WinServ.logExp (e, true);
        }
    }
}