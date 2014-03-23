
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// This thread is used to parse the incoming message and dispatch to the right handling thread
public class Server_ProcThread implements Runnable {
    
    // @lfred: the key data structure in async processing
    LinkedBlockingQueue<Server_ClntMsg> m_cmdQueue;
        
    // @lfred: static variables
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
    
    // ugly: handle functions for individual msgs
    void processMsg_login (Server_ClntMsg sCmd) {
        
        Server_DataRepo repo = Server_DataRepo.getDataRepo ();
        Server.log ("LOGIN_REQ");
            
        String name = sCmd.getMsgAt (1);
        String pass = sCmd.getMsgAt (2);
        
        if (repo.isValidUser (name, pass) == true) {
            
            // check if logged-in already, If so, just returned the session id
            int sessionId = repo.userLoggedIn (name);
            Server.log ("login okay: " + sessionId);
        } else {
            Server.log ("login failed");
        }
    }
    
    void processMsg_createClass (Server_ClntMsg sCmd) {
        
        Server_DataRepo repo = Server_DataRepo.getDataRepo ();
        Server.log ("LOGIN_REQ");
            
        String className = sCmd.getMsgAt (1);
        int userSession = Integer.parseInt (sCmd.getMsgAt (2));
        
        if (repo.isClassExist (className) == true) {
            //TODO: should return an existing ID or return error
            return;
        } else {
            //TODO: to create the classroom and related tables
            return;
        }
    }
    
    void readMsg (Server_ClntMsg sCmd) {
        
        String data;
        
        try {    
            while ((data = sCmd.getReader().readLine ()) != null) {
                
                data.trim ();
                
                if (data.equals ("END") == true) 
                    break;
                                    
                sCmd.pushMsg (data);
            }
        } catch (Exception e) {
            Server.log ("Exception: " + e);
            e.printStackTrace ();
        }
    }
    
    // main processing functions
    void processMsg (Server_ClntMsg sCmd) {
        
        String msgType = sCmd.getMsgAt (0);
        
        switch (sCmd.getMsgType ()) {
            case LOGIN_REQ:
                processMsg_login (sCmd);
            break;
                
            case LOGOUT_REQ:
            break;
            
            case CREATE_CLASS_REQ:
                processMsg_createClass (sCmd);
            break;
            
            case LIST_CLASS_REQ:
            break;
            
            case DEL_CLASS_REQ:
            break;
            
            case JOIN_CLASS_REQ:
            break;
            
            case QUERY_CLASS_INFO_REQ:
            break;
            
            case QUIT_CLASS_REQ:
            break;
            
            case KICK_USER_REQ:
            break;
            
            case PUSH_CONTENT_REQ:
            break;
            
            case PUSH_CONTENT_GET_REQ:
            break;
            
            case COND_PUSH_CONTENT_REQ:
            break;
            
            case GET_PRESENT_TOKEN_REQ:
            break;
            
            case CHANGE_PRESENT_TOKEN_RES:
            break;
            
            case RETRIEVE_PRESENT_TOKEN_REQ:
            break;
            
            default:
                Server.logErr ("Unknown command");
            break;
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
            readMsg (sCmd);
            processMsg (sCmd);
        }
    }
}
