import java.util.*; 

public class WinServ_DataRepo {

    static WinServ_DataRepo sm_repo = null;
    
    String m_userName;
    int    m_cookieId;
    
    String m_currentClassName;
    int    m_currentClassId;
    
    boolean m_isLoggedIn;
    boolean m_isInstructor;
    boolean m_isPresenter;
    
    Vector<String>  m_classes;
    Vector<Integer> m_classIds;
    Vector<String>  m_classInsts;
    
    String          m_instInClass;
    Vector<String>  m_peopleInClass;
    Vector<Integer> m_peopleInClassIds;

    public static WinServ_DataRepo getDataRepo () {
        
        if (sm_repo == null) {
            sm_repo = new WinServ_DataRepo ();
        }
        
        return sm_repo;
    }

    private WinServ_DataRepo () {
        
        m_userName = null;
        m_isLoggedIn = false;
        m_isInstructor = false;
        m_isPresenter = false;
        
        m_classes = new Vector<String> ();
        m_classIds = new Vector<Integer> ();
        m_classInsts = new Vector<String> ();
        
        m_peopleInClass = new Vector<String> ();
        m_peopleInClassIds = new Vector<Integer> ();
    }
    
    // setters & getters
    public String getUserName () {
        return m_userName;
    }
    
    public void setUserName (String name) {
        m_userName = name;
    }
    
    public boolean isLoggedIn () {
        return m_isLoggedIn;
    }
    
    public void setCookieId (int id) {
        m_cookieId = id;
    }
    
    public int getCookieId () {
        return m_cookieId;
    }
    
    public void setCurrentClass (int id, String name) {
        m_currentClassId = id;
        m_currentClassName = name;
    }
    
    public int getCurrentClassId () {
        return m_currentClassId;
    }
    
    public String getCurrentClassName () {
        return m_currentClassName;
    }
    
    public void setLoggedIn (boolean login) {
        m_isLoggedIn = login;
    }
    
    public boolean isInstructor () {
        return m_isInstructor;
    }
    
    public void setInstructor (boolean inst) {
        m_isInstructor = inst;
    } 
    
    public boolean isPresenter () {
        return m_isPresenter;
    }
    
    public void setPresenter (boolean present) {
        m_isPresenter = present;
    }
    
    // class operators
    public final Vector<String> getClasses () {
        return m_classes;
    }
    
    public final Vector<Integer> getClassIds () {
        return m_classIds;
    }
    
    public final Vector<String> getClassInsts () {
        return m_classInsts;
    }
    
    public void insertClass (String name, int id, String inst) {
        m_classes.add (name);
        m_classIds.add (id);
        m_classInsts.add (inst);
    }
    
    public void clearClasses () {
        m_classes.clear ();
        m_classIds.clear ();
        m_classInsts.clear ();
    }
    
    // prople operators
    public String getInstName () {
        return m_instInClass;
    }
    
    public void setInstName (String n) {
        m_instInClass = n;
    }
    
    public final Vector<String> getPeopleNamesInClass () {
        return m_peopleInClass;
    }
    
    public final Vector<Integer> getPeopleIdInClass () {
        return m_peopleInClassIds;
    }
    
    public void insertPersonInClass (String name, int id) {
        m_peopleInClass.add (name);
        m_peopleInClassIds.add (id);
    }
    
    public void clearPeopleInClass () {
        m_peopleInClass.clear ();
        m_peopleInClassIds.clear ();
    }
}