
public class WinServ_studentData {

    int m_stdntId;
    String m_stdntName;
    
    public WinServ_studentData (int id, String name) {
        m_stdntId = id;
        m_stdntName = name;
    }
    
    public int getId () {
        return m_stdntId;
    }
    
    public String getName () {
        return m_stdntName;
    }
    
    @Override
    public String toString () {
        return m_stdntName;
    }
}