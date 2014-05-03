
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

public class WinServ_ControlPanel extends JFrame 
    implements ActionListener, WinServ_MsgHandler, ChangeListener, PC_SimpleMsgHandler {
    
    final static String BUCKET_NAME       = "CloudClassRoom";
    final static String COLON             = ":";
    final static String SUCCESS           = "SUCCESS";
    final static String END               = "END";
    final static String LOGOUT_REQ        = "LOGOUT_REQ";
    final static String LIST_CLASS_REQ    = "LIST_CLASS_REQ";
    final static String LIST_CLASS_RES    = "LIST_CLASS_RES";
    final static String CREATE_CLASS_REQ  = "CREATE_CLASS_REQ";
    final static String CREATE_CLASS_RES  = "CREATE_CLASS_RES";
    final static String JOIN_CLASS_REQ    = "JOIN_CLASS_REQ";
    final static String JOIN_CLASS_RES    = "JOIN_CLASS_RES";
    final static String DEL_CLASS_REQ     = "DELETE_CLASS_REQ";
    final static String DEL_CLASS_RES     = "DELETE_CLASS_RES";
    final static String QUERY_CLASS_INFO_REQ = "QUERY_CLASS_INFO_REQ";
    final static String QUERY_CLASS_INFO_RES = "QUERY_CLASS_INFO_RES";
    final static String QUIT_CLASS_REQ    = "QUIT_CLASS_REQ";
    final static String QUIT_CLASS_RES    = "QUIT_CLASS_RES";
    
    final static String GET_PRESENT_TOKEN_REQ = "GET_PRESENT_TOKEN_REQ";
    final static String GET_PRESENT_TOKEN_RES = "GET_PRESENT_TOKEN_RES";
    
    final static String JOIN_CLASS_APPROVAL_REQ = "JOIN_CLASS_APPROVAL_REQ";
    final static String JOIN_CLASS_APPROVAL_RES = "JOIN_CLASS_APPROVAL_RES";
    
    final static String KICK_USER_REQ = "KICK_USER_REQ";
    final static String KICK_USER_RES = "KICK_USER_RES"; 
    
    final static String RETRIEVE_PRESENT_TOKEN_REQ = "RETRIEVE_PRESENT_TOKEN_REQ";
    final static String RETRIEVE_PRESENT_TOKEN_RES = "RETRIEVE_PRESENT_TOKEN_RES";
    
    final static String CHANGE_PRESENT_TOKEN_REQ = "CHANGE_PRESENT_TOKEN_REQ";
    final static String CHANGE_PRESENT_TOKEN_RES = "CHANGE_PRESENT_TOKEN_RES";
     
    final static String PUSH_CONTENT_REQ = "PUSH_CONTENT_REQ";
    final static String PUSH_CONTENT_NOTIFY = "PUSH_CONTENT_NOTIFY";
    final static String PUSH_CONTENT_GET_REQ = "PUSH_CONTENT_GET_REQ";
    final static String PUSH_CONTENT_GET_RES = "PUSH_CONTENT_GET_RES";
    
    // classes control buttons
    JList<String> m_classList;
    JButton m_listClassBtn;
    JButton m_createClassBtn;
    JButton m_deleteClassBtn;
    JButton m_joinClassBtn;
    
    // in-class control button
    JList<String> m_studentList;
    JButton m_kickStudentBtn;
    JButton m_queryClassBtn;
    JButton m_leaveClassBtn;
    JButton m_reqPresenterBtn;
    
    // tabbed panel
    JTabbedPane m_tabPan;
    
    // general button
    JButton m_logoutBtn;
    JButton m_codeEditorBtn;
    JButton m_imgViewerBtn;    
    
    static WinServ_ControlPanel sm_ctrlPanel;
    
    static {
        sm_ctrlPanel = null;
    }
    
    public static WinServ_ControlPanel getCtrlPanel () {
        
        if (sm_ctrlPanel == null) {
            sm_ctrlPanel = new WinServ_ControlPanel ();
        }
        
        return sm_ctrlPanel;
    }
    
    private WinServ_ControlPanel () {
        configDataComponents ();
        configUiComponents ();
    }
    
    void configDataComponents () {
        
        // starting my own receiver thread
        PC_SimpleReceiver.startReceiver (WinServ_SysParam.M_CTL_VIEW_PORT, this);

        // register server events
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        ntfServ.registerMsgHandler (JOIN_CLASS_APPROVAL_REQ, this);
        ntfServ.registerMsgHandler (CHANGE_PRESENT_TOKEN_REQ, this);
        ntfServ.registerMsgHandler (PUSH_CONTENT_NOTIFY, this); 
    }
    
    void configUiComponents () {
        
        JPanel content = new JPanel ();
        content.setLayout (new BorderLayout ());
        setContentPane (content);
        
        // Toolbar config
        JToolBar toolBar = new JToolBar ();
        toolBar.setMargin (new Insets (2, 2, 2, 2));
        toolBar.setBorderPainted (true);
        toolBar.setFloatable (false);
        add (toolBar, BorderLayout.NORTH);
        
        m_logoutBtn = new JButton (new ImageIcon (WinServ_SysParam.gtResPath ("logout.png")));
        m_logoutBtn.addActionListener (this);
        toolBar.add (m_logoutBtn);
        toolBar.addSeparator ();
        
        m_codeEditorBtn = new JButton (new ImageIcon (WinServ_SysParam.gtResPath ("code.png")));
        m_codeEditorBtn.addActionListener (this);
        toolBar.add (m_codeEditorBtn);
        
        m_imgViewerBtn = new JButton (new ImageIcon (WinServ_SysParam.gtResPath ("pic.png")));
        m_imgViewerBtn.addActionListener (this);
        toolBar.add (m_imgViewerBtn);
        
        // tabs
        m_tabPan = new JTabbedPane (JTabbedPane.TOP);
        m_tabPan.addChangeListener (this);
        add (m_tabPan);
        
        JPanel panel_allClasses = new JPanel ();
        m_tabPan.add ("all classes", panel_allClasses);
        
        GridBagLayout classes_gridbag = new GridBagLayout ();
        GridBagConstraints classes_c = new GridBagConstraints ();
        panel_allClasses.setLayout (classes_gridbag);
        
        // process all class panels
        m_classList = new JList<String> ();
        m_classList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        m_classList.setLayoutOrientation (JList.VERTICAL_WRAP);
        m_classList.setVisibleRowCount (-1);
        m_classList.setPrototypeCellValue ("This is a ver long long long long long string");
        classes_c.gridwidth = 4;                //reset to the default
        classes_c.gridheight = 9;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 0;
        classes_c.gridy = 0;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_classList, classes_c);
        panel_allClasses.add (m_classList);
        
        m_listClassBtn = new JButton ("List Class");
        m_listClassBtn.addActionListener (this);
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 5;
        classes_c.gridy = 0;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_listClassBtn, classes_c);
        panel_allClasses.add (m_listClassBtn);
        
        m_createClassBtn = new JButton ("Create Class");
        m_createClassBtn.addActionListener (this);
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 5;
        classes_c.gridy = 1;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_createClassBtn, classes_c);
        panel_allClasses.add (m_createClassBtn);
        
        m_deleteClassBtn = new JButton ("Delete Class");
        m_deleteClassBtn.addActionListener (this);
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 5;
        classes_c.gridy = 2;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_deleteClassBtn, classes_c);
        panel_allClasses.add (m_deleteClassBtn);
        
        m_joinClassBtn = new JButton ("Join Class");
        m_joinClassBtn.addActionListener (this);
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 5;
        classes_c.gridy = 3;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_joinClassBtn, classes_c);
        panel_allClasses.add (m_joinClassBtn);
         
         // in-class panel
        JPanel panel_inClass = new JPanel ();
        m_tabPan.add ("in class", panel_inClass);
    
        GridBagLayout inClasses_gridbag = new GridBagLayout();
        GridBagConstraints inClasses_c = new GridBagConstraints();
        panel_inClass.setLayout (inClasses_gridbag);
        
        // process all class panels
        m_studentList = new JList<String> ();
        m_studentList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        m_studentList.setLayoutOrientation (JList.VERTICAL_WRAP);
        m_studentList.setVisibleRowCount (-1);
        inClasses_c.gridwidth = 4;                
        inClasses_c.gridheight = 9;
        inClasses_c.weighty = 0.9;
        inClasses_c.weightx = 1.0;
        inClasses_c.gridx = 0;
        inClasses_c.gridy = 1;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_studentList, inClasses_c);
        panel_inClass.add (m_studentList);
        
        m_kickStudentBtn = new JButton ("Kick Student");
        m_kickStudentBtn.addActionListener (this);
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 0.1;
        inClasses_c.gridx = 0;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_kickStudentBtn, inClasses_c);
        panel_inClass.add (m_kickStudentBtn);
        
        m_queryClassBtn = new JButton ("Query class");
        m_queryClassBtn.addActionListener (this);
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 0.1;
        inClasses_c.gridx = 1;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_queryClassBtn, inClasses_c);
        panel_inClass.add (m_queryClassBtn);
        
        m_leaveClassBtn = new JButton ("Leave class");
        m_leaveClassBtn.addActionListener (this);
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 0.1;
        inClasses_c.gridx = 2;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_leaveClassBtn, inClasses_c);
        panel_inClass.add (m_leaveClassBtn);
        
        m_reqPresenterBtn = new JButton ("Request Presenter");
        m_reqPresenterBtn.addActionListener (this);
        inClasses_c.gridwidth = 1;           
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 0.1;
        inClasses_c.gridx = 3;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_reqPresenterBtn, inClasses_c);
        panel_inClass.add (m_reqPresenterBtn);
        
        // Finalize UI config
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setTitle ("Cloud Classroom Control Panel");
        pack ();
        setLocationRelativeTo (null);
        setVisible (true);
        
        
        // UI config
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        if (repo.isInstructor ()) {
            m_joinClassBtn.setEnabled (false);
            m_leaveClassBtn.setEnabled (false);
        } else {
            m_kickStudentBtn.setEnabled (false);
        }
    }
    
    public void actionPerformed (ActionEvent e) {
        
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        Object src = e.getSource (); 
        int cookieId = repo.getCookieId ();
        
        if (src == m_codeEditorBtn) {
            PC_SimpleEditor.startEditor (true);
        } else if (src == m_imgViewerBtn) {
            PC_TinyImageViewer.startImgViewer (true);
        } else if (src == m_logoutBtn) {
            
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (LOGOUT_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (END);
            ntfServ.sendMsgToServer (cmd);
            System.exit (0);
            
        } else if (src == m_listClassBtn) {
            
            WinServ_WaitDialog.dialogFactory ("Please wait...");
            
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (LIST_CLASS_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (END);
            
            ntfServ.registerMsgHandler (LIST_CLASS_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_createClassBtn) {
            
            // small UI to let user input the name
            String s = 
                (String)JOptionPane.showInputDialog (
                    this,
                    "Input class name", "User Input",
                    JOptionPane.PLAIN_MESSAGE,
                    null, null, "class name");

            //If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
                cmd.pushStr (CREATE_CLASS_REQ);
                cmd.pushStr (COLON + s);
                cmd.pushStr (COLON + cookieId);
                cmd.pushStr (END);
                
                // do the NW
                ntfServ.registerMsgHandler (CREATE_CLASS_RES, this);
                ntfServ.sendMsgToServer (cmd);
                return;
            } else {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error. Please check again.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } else if (src == m_deleteClassBtn) {
            
            int selectIdx = m_classList.getSelectedIndex ();
            
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error, select a class first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int ids = repo.searchClassByInx (selectIdx).getId ();
            
            // create message
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (DEL_CLASS_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + ids); //TOD: to fix
            cmd.pushStr (END);
            
            // NW things
            ntfServ.registerMsgHandler (DEL_CLASS_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_joinClassBtn) {
            
            int selectIdx = m_classList.getSelectedIndex ();
            
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error, select a class first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            } 
            
            int ids = repo.searchClassByInx (selectIdx).getId ();
            
            // create message
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (JOIN_CLASS_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + ids);
            cmd.pushStr (END);
            
            // NW things
            ntfServ.registerMsgHandler (JOIN_CLASS_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_kickStudentBtn) {
            
            int stdntIdx = m_studentList.getSelectedIndex ();
            int classIdx = m_classList.getSelectedIndex ();
            
            if (stdntIdx == -1 || classIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error, please select a student and a class first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String stdnt = repo.searchStdntByInx (stdntIdx).getName ();
            int classId = repo.searchClassByInx (classIdx).getId ();
            
            // create message
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (KICK_USER_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + classId);
            cmd.pushStr (COLON + stdnt);
            cmd.pushStr (END);
            
            // NW things
            ntfServ.registerMsgHandler (KICK_USER_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_queryClassBtn) {
            
            repo.clearPeopleInClass ();
            
            int selectIdx = m_classList.getSelectedIndex ();
            
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error, select a class first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int ids = repo.searchClassByInx (selectIdx).getId ();
            
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (QUERY_CLASS_INFO_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + ids);
            cmd.pushStr (END);
            
            // NW things
            ntfServ.registerMsgHandler (QUERY_CLASS_INFO_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_leaveClassBtn) {
            
            int selectIdx = m_classList.getSelectedIndex ();
            
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Please select a class first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr (QUIT_CLASS_REQ);
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + selectIdx);
            cmd.pushStr (END);
            
            // NW things
            ntfServ.registerMsgHandler (QUERY_CLASS_INFO_RES, this);
            ntfServ.sendMsgToServer (cmd);
            
        } else if (src == m_reqPresenterBtn) {
            
            int selectIdx = m_classList.getSelectedIndex ();
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Please select a class first",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            int classId = repo.searchClassByInx (selectIdx).getId ();
            
            if (repo.isInstructor ()) {
                WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
                cmd.pushStr (RETRIEVE_PRESENT_TOKEN_REQ);
                cmd.pushStr (COLON + cookieId);
                cmd.pushStr (COLON + classId);
                cmd.pushStr (END);
            
                // NW things
                ntfServ.registerMsgHandler (RETRIEVE_PRESENT_TOKEN_RES, this);
                ntfServ.sendMsgToServer (cmd);
            } else {
                WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
                cmd.pushStr (GET_PRESENT_TOKEN_REQ);
                cmd.pushStr (COLON + cookieId);
                cmd.pushStr (COLON + classId);
                cmd.pushStr (END);
            
                // NW things
                ntfServ.registerMsgHandler (QUERY_CLASS_INFO_RES, this);
                ntfServ.sendMsgToServer (cmd);
            }
        }
    }
    
    public void stateChanged (ChangeEvent e) {
        
        /*
        if (m_classList != null && m_tabPan != null) {
            // UI sugar
            if (m_classList.getSelectedIndex () == -1 && m_tabPan.getSelectedIndex () == 1) {
                m_tabPan.setSelectedIndex (0);
            }
        }
        */
    }
    
    public void handleServerMsg (WinServ_ReqCommand cmd) {
        
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        int cookieId = repo.getCookieId ();
        
        String type = cmd.getStrAt (0);
        
        
        if (type.equals (LIST_CLASS_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            WinServ_WaitDialog.closeDialog ();
            parseListClassRes (cmd);
        } else if (type.equals (CREATE_CLASS_RES)) {  
            ntfServ.unregisterMsgHandler (type, this);          
            parseCreateClassReq (cmd);
        } else if (type.equals (DEL_CLASS_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            parseDelClassRes (cmd);
        } else if (type.equals (JOIN_CLASS_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            WinServ_WaitDialog.closeDialog ();
            parseJoinClassRes (cmd);
        } else if (type.equals (QUERY_CLASS_INFO_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            WinServ_WaitDialog.closeDialog ();
            parseQueryClassInfoRes (cmd);
        } else if (type.equals (JOIN_CLASS_APPROVAL_REQ)) {
            
            processApprovalReq (cmd);
        } else if (type.equals (KICK_USER_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            processKickRsp (cmd);
        } else if (type.equals (RETRIEVE_PRESENT_TOKEN_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            processRtrvPresentRsp (cmd); 
        } else if (type.equals (CHANGE_PRESENT_TOKEN_REQ)) {
            
            processChangePresentReq (cmd);
        } else if (type.equals (GET_PRESENT_TOKEN_RES)) {
            ntfServ.unregisterMsgHandler (type, this);
            processPresentTokenRes (cmd);
        } else if (type.equals (PUSH_CONTENT_NOTIFY)) {
            
            processPushContentNtf (cmd);
        } else {
            WinServ.logErr ("Unhandled message: " + type);
        }
    }
    
    void processKickRsp (WinServ_ReqCommand cmd) {
        
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        ntfServ.unregisterMsgHandler (KICK_USER_RES, this);
                
        String status = cmd.getStrAt (1).substring (1);
        
        if (status.equals (SUCCESS)) {
            JOptionPane.showMessageDialog (
                this,
                "Kick operation is completed.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        return;
    }
    
    void processApprovalReq (WinServ_ReqCommand cmd) {
        
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        
        String userName     = cmd.getStrAt (1).substring (1);
        String classId      = cmd.getStrAt (2).substring (1);
        String className    = cmd.getStrAt (3).substring (1);
        
        //default icon, custom title
        int ans = 
            JOptionPane.showConfirmDialog (
                this,
                "User, " + userName + ", would like to join class, " + className + " ?",
                "Approval Request",
                JOptionPane.YES_NO_OPTION);
        
        WinServ_ReqCommand rsp = new WinServ_ReqCommand ();
        rsp.pushStr (JOIN_CLASS_APPROVAL_RES);
        rsp.pushStr (":" + repo.getCookieId ());
        rsp.pushStr (":" + classId);
        rsp.pushStr (":" + userName);
                
        if (ans == JOptionPane.YES_OPTION) {
            rsp.pushStr (":true");
        } else {
            rsp.pushStr (":false");
        }
        rsp.pushStr (END);
        
        ntfServ.sendMsgToServer (rsp);
    }
    
    boolean parseListClassRes (WinServ_ReqCommand cmd) {
        
        String status = cmd.getStrAt (1).substring (1);
        
        if (status.equals (SUCCESS) == false) {
            JOptionPane.showMessageDialog (
                this,
                "List classes failed: " + status,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        int numClass  = Integer.parseInt (cmd.getStrAt(2).substring(1));
        int ln = 3;
        
        // clear data before proceeding
        repo.clearClasses ();
        
        for (int i=0; i<numClass; ++i) {
            
            String id   = cmd.getStrAt (ln++).substring (1);
            String name = cmd.getStrAt (ln++).substring (1);
            String inst = cmd.getStrAt (ln++).substring (1);
            
            WinServ.logInfo ("id: " + id);
            repo.insertClass (name, Integer.parseInt (id), inst);
        }
        
        // update List view
        m_classList.clearSelection ();
        repo.setClassList (m_classList);
        
        return true;
    }
    
    boolean parseDelClassRes (WinServ_ReqCommand cmd) {
        
        String status = cmd.getStrAt (1).substring (1);
        
        if (status.equals (SUCCESS) == false) {
            WinServ_WaitDialog.closeDialog ();
            JOptionPane.showMessageDialog (
                this,
                "Delete class failed: " + status,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        issueListCmd ();
        
        return true;
    }
    
    boolean parseJoinClassRes (WinServ_ReqCommand cmd) {
        
        String status = cmd.getStrAt (1).substring (1);
        
        if (status.equals (SUCCESS) == false) {
            JOptionPane.showMessageDialog (
                this,
                "Join class failed: " + status,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        String ln1 = cmd.getStrAt (2).substring (1);
        String ln2 = cmd.getStrAt (3).substring (1);
        
        // TODO: set repo params

        return true;
    }
    
    boolean parseQueryClassInfoRes (WinServ_ReqCommand cmd) {
        
        int idx = 1;
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        String status = cmd.getStrAt (idx++).substring (1);
        
        if (status.equals (SUCCESS) == false) {
            JOptionPane.showMessageDialog (
                this,
                "Query class failed: " + status,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String inst = cmd.getStrAt (idx++).substring(1);
        repo.setInstName (inst);
        //repo.insertStdntInClass (inst, 0);
        
        int stdntCnt = Integer.parseInt (cmd.getStrAt (idx++).substring (1));
        
        for (int i=0; i<stdntCnt; ++i) {
            String name = cmd.getStrAt (idx++).substring (1);
            repo.insertStdntInClass (name, i);
        }
        
        // update List view
        m_studentList.clearSelection ();
        repo.setStudentList (m_studentList);
        
        return true;
    }
    
    boolean parseCreateClassReq (WinServ_ReqCommand cmd) {
        WinServ.logInfo ("parseCreateClassReq");
        issueListCmd ();
        return true;
    }
    
    void processRtrvPresentRsp (WinServ_ReqCommand cmd) {
        
        String status = cmd.getStrAt (1).substring (1);
        
        if (status.equals (SUCCESS) == false) {
            JOptionPane.showMessageDialog (
                this,
                "Presentation token retrieval failed: " + status,
                "Status",
                JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog (
                this,
                "Presentation token retrieval done",
                "Status",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        return;
    }
    
    void processPresentTokenRes (WinServ_ReqCommand cmd) {
        
        String ln1 = cmd.getStrAt (1).substring (1);
        String ln2 = cmd.getStrAt (2).substring (1);
        String ln3 = cmd.getStrAt (3).substring (1);
        
        if (ln3.equals (SUCCESS)) {
            JOptionPane.showMessageDialog (
                this,
                "You have the presentation right now.",
                "Status",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog (
                this,
                "Sorry, the presentation right is not given.",
                "Status",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        return;
    }
    
    void processChangePresentReq (WinServ_ReqCommand cmd) {
        
        WinServ.logErr ("processChangePresentReq");
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        
        String userName = cmd.getStrAt (1).substring (1);
        String classId  = cmd.getStrAt (2).substring (1);
        int cookieId = repo.getCookieId ();
        
        int n = 
            JOptionPane.showConfirmDialog (
                this,
                "The user," + userName + " , is requesting presentation right. Okay?",
                "Presentation",
                JOptionPane.YES_NO_OPTION);
        
        WinServ_ReqCommand rss = new WinServ_ReqCommand ();
        
        rss.pushStr (CHANGE_PRESENT_TOKEN_RES);
        rss.pushStr (COLON + cookieId);
        rss.pushStr (COLON + classId);
        rss.pushStr (COLON + userName);
        
        if (n == JOptionPane.YES_OPTION) {
            rss.pushStr (COLON + "true");
        } else {
            rss.pushStr (COLON + "false");
        }
        
        rss.pushStr (END);
    
        ntfServ.sendMsgToServer (rss);
    }
    
    void processPushContentNtf (WinServ_ReqCommand cmd) {
        
        String contentName = cmd.getStrAt (2).substring (1);
        String localName = WinServ_SysParam.getFsPath (contentName);
        
        boolean ret = 
            WinServ_CloudHelper.downloadFile (
                localName, 
                WinServ_SysParam.M_BKT_NAME, 
                contentName);
                 
        if (ret == false) {
            WinServ.logErr ("Download file failure");
        } else {
            
            // name suffix resolver
            if (WinServ_SysParam.isImageFile (contentName)) {
                // notify the image viewer
                PC_TinyImageViewer.startImgViewer (true);
                
                // we need to tell the viewrt to show
                
            } else {
                // notify the text viewer
                PC_SimpleEditor.startEditor (true);
 
                try {
                    // we need to tell the viewr to show
                    WinServ_SysParam.sendMsg (
                        PC_TinyImageViewer.M_MSG_UPDATE, 
                        localName, 
                        WinServ_SysParam.M_TXT_VIEW_PORT);
                        
                } catch (Exception e) {
                    WinServ.logExp (e, false);
                }
            }
        }
    }
    
    void issueListCmd () {
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        int cookieId = repo.getCookieId ();
        
        WinServ_WaitDialog.dialogFactory ("Please wait...");
     
        // issue list command after create
        WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
        cmd.pushStr (LIST_CLASS_REQ);
        cmd.pushStr (COLON + cookieId);
        cmd.pushStr (END);
            
        ntfServ.registerMsgHandler (LIST_CLASS_RES, this);
        ntfServ.sendMsgToServer (cmd);
    }
    
    @Override
    public void simpleMsgHandler (String msg) {
        
        String update_ntf = "UPDATE_NTF:";
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        
        // We handle: UPDATE_NTF: fname 
        if (msg.startsWith (update_ntf)) {
            String fname = msg.substring (update_ntf.length ());
            
            int selectIdx = m_classList.getSelectedIndex ();
            
            if (selectIdx == -1) {
                JOptionPane.showMessageDialog (
                    this,
                    "Input error, select a class first.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int ids = repo.searchClassByInx (selectIdx).getId ();
            int cookieId = repo.getCookieId ();
        
            // issue list command after create
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr ("PUSH_CONTENT_REQ");
            cmd.pushStr (COLON + cookieId);
            cmd.pushStr (COLON + ids);
            cmd.pushStr (COLON + fname);
            cmd.pushStr (COLON + "IMAGE");
            cmd.pushStr (END);
            ntfServ.sendMsgToServer (cmd);
        } 
    }
}