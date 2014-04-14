import java.util.*; 

public class WinServ_DataRepo {

    static WinServ_DataRepo sm_repo = null;
    
    String m_userName;
    int    m_cookieId;
    int    m_currentClassId;
    
    boolean m_isLoggedIn;
    boolean m_isInstructor;
    boolean m_isPresenter;
    
    Vector<String>  m_classes;
    Vector<Integer> m_classIds;
    
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
    
    public void setClassId (int id) {
        m_currentClassId = id;
    }
    
    public int getClassId () {
        return m_currentClassId;
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
    
    public Vector<String> getClasses () {
        return m_classes;
    }
    
    public void setClasses (Vector<String> input) {
        
        m_classes.clear ();
        
        for (String s : input) {
            m_classes.add (s);
        }
    }
    
    public void clearClasses () {
        m_classes.clear ();
    }
    
    public Vector<String> getPeopleInClass () {
        return m_peopleInClass;
    }
    
    public void setPeopleInClass (Vector<String> pepl) {
        
        m_peopleInClass.clear ();
        
        for (String s : pepl) {
            m_peopleInClass.add (s);
        }
    }
    
    public void clearPeopleInClass () {
        m_peopleInClass.clear ();
    }
}