
public class WinServ_classData {

    int m_clsId;
    String m_clsName;
    String m_instName;
    
    public WinServ_classData (int id, String name, String inst) {
        m_clsId = id;
        m_clsName = name;
        m_instName = inst;
    }
    
    public int getId () {
        return m_clsId;
    }
    
    public String getName () {
        return m_clsName;
    }
    
    public String getInst () {
        return m_instName;
    }
    
    @Override
    public String toString () {
        return m_clsName;
    }
}