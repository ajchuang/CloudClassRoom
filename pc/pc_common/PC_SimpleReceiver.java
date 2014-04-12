package pc_common;

import java.io.*;
import java.net.*;
import java.util.*;

// runnging a simple UDP server to the information from other process
public class PC_SimpleReceiver implements Runnable {
    
    int m_port; 
    PC_SimpleMsgHandler m_hdlr;
    static int DEFAULT_MSG_SIZE = 1024;
    
    public static void startReceiver (int port, PC_SimpleMsgHandler hdlr) {
        new Thread (new PC_SimpleReceiver (port, hdlr)).start ();
    }
    
    private PC_SimpleReceiver (int port, PC_SimpleMsgHandler hdlr) {
        m_port = port;
        m_hdlr = hdlr;
    }

    public void run () {
        
        try {
            DatagramSocket socket = new DatagramSocket (m_port);
            
            while (true) {
                byte[] receiveData = new byte[DEFAULT_MSG_SIZE];
                DatagramPacket receivePacket = new DatagramPacket (receiveData, receiveData.length);
                socket.receive (receivePacket);
                
                // notification data received.
                String str = new String (receivePacket.getData(), "UTF-8");
                m_hdlr.simpleMsgHandler (str);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }
}
