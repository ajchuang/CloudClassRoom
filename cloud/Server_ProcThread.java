
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// This thread is used to parse the incoming message and dispatch to the right handling thread
public class Server_ProcThread implements Runnable {
    
    // @lfred: the key data structure in async processing
    LinkedBlockingQueue<Server_ClntMsg> m_cmdQueue;
    
    static Server_ProcThread m_procThread = null;

    // @lfred: yet another singleton trick
    Server_ProcThread () {
        m_cmdQueue = new LinkedBlockingQueue<Server_ClntMsg> ();
    }
    
    public static Server_ProcThread getProcThread () {
        
        if (m_procThread == null)
            m_procThread = new Server_ProcThread ();
        
        return m_procThread;
    }
    
    public static void enqueueCmd (Server_ClntMsg cmd) {
        try {
            getProcThread ().m_cmdQueue.put (cmd);
        } catch (Exception e) {
            Server.logErr ("enqueueCmd: Exception - " + e);
            e.printStackTrace ();
        }
    }
    
    void processMsg (Server_ClntMsg sCmd) {
        
        String msgType = sCmd.getMsgAt (0);
        
        if (msgType.equals ("LOGIN_REQ") == true) {
            Server.log ("LOGIN_REQ");
        } else if (msgType.equals ("LOGOUT_REQ") == true) {
            Server.log ("LOGOUT_REQ");
        }
    }
    
    public void run () {
        
        while (true) {
            
            Server_ClntMsg sCmd;

            try {
                sCmd = m_cmdQueue.take ();
            } catch (Exception e) {
                Server.log ("Dequeue Exception - " + e);
                e.printStackTrace ();
                continue;
            }
            
            // @lfred: dispatch the command to the right thread.
            Server.log ("Get the object to dispatch");
            processMsg (sCmd);
        }
    }

}