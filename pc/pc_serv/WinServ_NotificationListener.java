
import java.util.*;
import java.net.*;
import java.io.*;

public class WinServ_NotificationListener implements Runnable {
    
    int m_port; 
    static int DEFAULT_NFT_SIZE = 1024;
    
    public WinServ_NotificationListener (int port) {
        m_port = port;
    }
    
    public void process_NTF (String str) {
        
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
    }
    
    public void run () {
        
        try {
            DatagramSocket socket = new DatagramSocket (m_port);
            
            while (true) {
                byte[] receiveData = new byte[DEFAULT_NFT_SIZE];
                DatagramPacket receivePacket = new DatagramPacket (receiveData, receiveData.length);
                socket.receive (receivePacket);
                
                // notification data received.
                // check what the type of the message is, and send to the main thread and request information
                String str = new String (receivePacket.getData(), "UTF-8");
                System.out.println ("[NTF] " + str);
                process_NTF (str);
            }
        } catch (Exception e) {
            WinServ.logExp (e, true);
        }
    }
}