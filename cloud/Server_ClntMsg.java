
import java.util.*;

public class Server_ClntMsg {

    Vector<String> m_msgVec;
    
    final static HashMap<String, Server_CmdType> sm_cmdMap;
    
    // static initializer
    static {
        sm_cmdMap = new HashMap<String, Server_CmdType> ();
        sm_cmdMap.put ("LOGIN_REQ", Server_CmdType.LOGIN_REQ); 
        sm_cmdMap.put ("LOGOUT_REQ", Server_CmdType.LOGOUT_REQ);
        sm_cmdMap.put ("CREATE_CLASS_REQ", Server_CmdType.CREATE_CLASS_REQ);
        sm_cmdMap.put ("LIST_CLASS_REQ", Server_CmdType.LIST_CLASS_REQ); 
        sm_cmdMap.put ("DEL_CLASS_REQ", Server_CmdType.DEL_CLASS_REQ);
        sm_cmdMap.put ("JOIN_CLASS_REQ", Server_CmdType.JOIN_CLASS_REQ);
        sm_cmdMap.put ("QUERY_CLASS_INFO_REQ", Server_CmdType.QUERY_CLASS_INFO_REQ);
        sm_cmdMap.put ("QUIT_CLASS_REQ", Server_CmdType.QUIT_CLASS_REQ);
        sm_cmdMap.put ("KICK_USER_REQ", Server_CmdType.KICK_USER_REQ);
        sm_cmdMap.put ("PUSH_CONTENT_REQ", Server_CmdType.PUSH_CONTENT_REQ);
        sm_cmdMap.put ("PUSH_CONTENT_GET_REQ", Server_CmdType.PUSH_CONTENT_GET_REQ);
        sm_cmdMap.put ("COND_PUSH_CONTENT_REQ", Server_CmdType.COND_PUSH_CONTENT_REQ);
        sm_cmdMap.put ("GET_PRESENT_TOKEN_REQ", Server_CmdType.GET_PRESENT_TOKEN_REQ); 
        sm_cmdMap.put ("CHANGE_PRESENT_TOKEN_RES", Server_CmdType.CHANGE_PRESENT_TOKEN_RES);
        sm_cmdMap.put ("RETRIEVE_PRESENT_TOKEN_REQ", Server_CmdType.RETRIEVE_PRESENT_TOKEN_REQ);
    }
    
    public Server_ClntMsg () {
        m_msgVec = new Vector<String> (); 
    }
    
    public Server_CmdType getMsgType () {
        
        if (m_msgVec.size() > 1) {
            return sm_cmdMap.get (m_msgVec.elementAt (0));
        } else
            return Server_CmdType.INVALID_SERVER_CMD;
    }
    
    public void pushMsg (String msg) {
        m_msgVec.add (msg);
    }
    
    public String getMsgAt (int idx) {
        return m_msgVec.elementAt (idx);
    }
}