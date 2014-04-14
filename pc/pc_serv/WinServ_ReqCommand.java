
import java.util.*;

public class WinServ_ReqCommand {
    
    Vector<String> m_data;
    
    public WinServ_ReqCommand () {
        m_data = new Vector<String> ();
    }
    
    void pushStr (String s) {
        m_data.add (s);
    }
    
    public int totalStrNum () {
        return m_data.size ();
    }
    
    public String getStrAt (int idx) {
        if (idx < m_data.size())
            return m_data.elementAt (idx);
        else {
            WinServ.logErr ("Getting non-existing string");
            System.exit (0);
            return null;
        }
    }
    
}