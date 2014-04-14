
import java.util.*;
import java.net.*;
import java.io.*;

public class WinServ_NtfServer implements Runnable {
    
    static int DEFAULT_NFT_SIZE = 1024;
    static WinServ_NtfServer sm_serv = null;
    
    // NW variables
    int m_port; 
    String m_addr;
    Socket m_socket;
    PrintWriter m_outputStream;
    BufferedReader m_inputStream;
    
    // concurrent lock
    Object m_lock;
    
    // MSG data structure
    HashMap<String, LinkedList<WinServ_MsgHandler>> m_msgMap;
    
    // @lfred: a lam singleton.
    public static WinServ_NtfServer getNtfServ () {
        
        if (sm_serv == null) {
            WinServ.logErr ("Access NTF server before creation");
            System.exit (0);
        }
        
        return sm_serv;
    }
    
    public static WinServ_NtfServer ntfFactory (String addr, int port) {
        
        if (sm_serv != null)
            return sm_serv;
        
        sm_serv = new WinServ_NtfServer (addr, port);
        return sm_serv;
    }
    
    private WinServ_NtfServer (String addr, int port) {
        
        if (sm_serv == null) {
            sm_serv = this;
        } else {
            WinServ.logErr ("Double creation of WinServ_NtfServer");
            System.exit (0);
        }
            
        m_addr = addr;
        m_port = port;
        m_lock = new Object ();
        m_msgMap = new HashMap<String, LinkedList<WinServ_MsgHandler>> ();
    }
    
    // @lfred: for people to register the event handler
    synchronized public void registerMsgHandler (String msg, WinServ_MsgHandler handler) {
        
        synchronized (m_lock) {
            
            WinServ.logInfo ("registerMsgHandler - Begin");
            
            LinkedList<WinServ_MsgHandler> hdlr = m_msgMap.get (msg);
            
            if (hdlr == null) {
                // nobody register this event before
                LinkedList<WinServ_MsgHandler> newHdlr = new LinkedList<WinServ_MsgHandler> ();
                newHdlr.add (handler);
                m_msgMap.put (msg, newHdlr);
                WinServ.logInfo ("registerMsgHandler - completed.1");
            } else {
                // someone has register this event
                
                if (hdlr.contains (handler) == true) {
                    // it's already there.
                    WinServ.logInfo ("registerMsgHandler - nothing to be done");
                } else {
                    hdlr.add (handler);
                    WinServ.logInfo ("registerMsgHandler - completed.2");
                }
            }
            
            WinServ.logInfo ("registerMsgHandler - End");
        } 
    }
    
    synchronized public void unregisterMsgHandler (String msg, WinServ_MsgHandler handler) {
        
        synchronized (m_lock) {
            WinServ.logInfo ("unregisterMsgHandler - Begin");
            
            LinkedList<WinServ_MsgHandler> hdlr = m_msgMap.get (msg);
            
            if (hdlr != null && hdlr.remove (handler)) {
                WinServ.logInfo ("unregisterMsgHandler - Removal done");
                
                // When nobody registers this event, just clean it up.
                if (hdlr.size () == 0) {
                    m_msgMap.remove (msg);
                }
                
            } else {
                WinServ.logInfo ("unregisterMsgHandler - Nothing to be done");
            }
            
            WinServ.logInfo ("unregisterMsgHandler - End");
        } 
    }
    
    synchronized public void sendMsgToServer (WinServ_ReqCommand cmd) {
        
        synchronized (m_lock) {
            WinServ.logInfo ("sendMsgToServer - Begin");
        
            for (int i = 0; i < cmd.totalStrNum (); ++i) {
                m_outputStream.println (cmd.getStrAt (i));
            }
        
            WinServ.logInfo ("sendMsgToServer - End");
        }
    }
    
    public void process_NTF (WinServ_ReqCommand cmd) {
        
        String cmdType = cmd.getStrAt (0);
        LinkedList<WinServ_MsgHandler> hdlrs = null;
        
        synchronized (m_lock) {
            WinServ.logInfo ("process_NTF - Begin");
            
            hdlrs = m_msgMap.get (cmdType);
            
            if (hdlrs != null) {
                for (WinServ_MsgHandler hdl : hdlrs) {
                    hdl.handleServerMsg (cmd);
                }
            }
            
            WinServ.logInfo ("process_NTF - End");
        }
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