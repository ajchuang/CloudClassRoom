
import java.util.*;

public class Server_ClntMsg {

    Vector<String> m_msgVec;
    
    public Server_ClntMsg () {
        m_msgVec = new Vector<String> (); 
    }
    
    public void pushMsg (String msg) {
        m_msgVec.add (msg);
    }
    
    public String getMsgAt (int idx) {
        return m_msgVec.elementAt (idx);
    }
}