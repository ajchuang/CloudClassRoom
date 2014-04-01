
import java.util.*;
import java.net.*;
import java.io.*;

public class WinServ_NotificationListener implements Runnable {
    
    int m_port; 
    static int DEFAULT_PORT = 7788;
    static int DEFAULT_NFT_SIZE = 1024;
    
    public WinServ_NotificationListener (int port) {
        m_port = port;
    }
    
    public WinServ_NotificationListener () {
        m_port = DEFAULT_PORT;
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
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}