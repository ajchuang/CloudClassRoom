import java.util.*; 
import javax.swing.JList;

public class WinServ_DataRepo {

    static WinServ_DataRepo sm_repo = null;
    
    String m_userName;
    int    m_cookieId;
    
    boolean m_isLoggedIn;
    boolean m_isInstructor;
    boolean m_isPresenter;
    
    Vector<WinServ_classData> m_classVec;
    
    String m_instInClass;
    Vector<WinServ_studentData> m_stdntInClass;

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
        
        m_classVec = new Vector<WinServ_classData> ();
        m_stdntInClass = new Vector<WinServ_studentData> ();
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
    
    //public void setCurrentClass (int id, String name) {
    //    m_currentClassId = id;
    //    m_currentClassName = name;
    //}
    
    //public int getCurrentClassId () {
    //    return m_currentClassId;
    //}
    
    //public String getCurrentClassName () {
    //    return m_currentClassName;
    //}
    
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
        //return m_isPresenter;
        return true;
    }
    
    public void setPresenter (boolean present) {
        m_isPresenter = present;
    }
    
    // class operators
    public void setClassList (JList uiComp) {
        uiComp.setListData (m_classVec);
    }
    
    public final WinServ_classData searchClassByInx (int idx) {
        return m_classVec.elementAt (idx);
    }
    
    public void insertClass (String name, int id, String inst) {
        m_classVec.add (new WinServ_classData (id, name, inst));
    }
    
    public void clearClasses () {
        m_classVec.clear ();
    }
    
    // people operators
    public String getInstName () {
        return m_instInClass;
    }
    
    public void setInstName (String n) {
        m_instInClass = n;
    }
    
    public void setStudentList (JList uiComp) {
        uiComp.setListData (m_stdntInClass);
    }
    
    
    
    public final WinServ_studentData searchStdntByInx (int idx) {
        return m_stdntInClass.elementAt (idx);
    }
    
    public void insertStdntInClass (String name, int id) {
        m_stdntInClass.add (new WinServ_studentData (id, name));
    }
    
    public void clearPeopleInClass () {
        m_stdntInClass.clear ();
    }
}