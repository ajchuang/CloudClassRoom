
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;

public class WinServ_ControlPanel extends JFrame implements ActionListener {
    
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
    
    // data variables. TODO: fix this with data repo
    boolean m_isLoggedIn;
    boolean m_isInstructor;
    boolean m_isPresenter;
    Vector<String> m_classes;
    Vector<String> m_peopleInClass;
    
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
        m_classes = new Vector<String> ();
        m_classes.add ("<no classes>");
        
        m_peopleInClass = new Vector<String> ();
        m_peopleInClass.add ("<no one here>");
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
        
        m_logoutBtn = new JButton (new ImageIcon ("res/logout.png"));
        m_logoutBtn.addActionListener (this);
        toolBar.add (m_logoutBtn);
        toolBar.addSeparator ();
        
        // tabs
        m_tabPan = new JTabbedPane (JTabbedPane.TOP);
        add (m_tabPan);
        
        JPanel panel_allClasses = new JPanel ();
        m_tabPan.add ("all classes", panel_allClasses);
        
        GridBagLayout classes_gridbag = new GridBagLayout();
        GridBagConstraints classes_c = new GridBagConstraints();
        panel_allClasses.setLayout (classes_gridbag);
        
        // process all class panels
        m_classList = new JList<String> (m_classes);
        m_classList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        m_classList.setLayoutOrientation (JList.HORIZONTAL_WRAP);
        m_classList.setVisibleRowCount (-1);
        classes_c.gridwidth = 3;                //reset to the default
        classes_c.gridheight = 5;
        classes_c.weighty = 1.0;
        classes_c.weightx = 1.0;
        classes_c.gridx = 0;
        classes_c.gridy = 0;
        classes_c.fill = GridBagConstraints.BOTH;
        classes_gridbag.setConstraints (m_classList, classes_c);
        panel_allClasses.add (m_classList);
        
        m_listClassBtn = new JButton ("List Class");
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.gridx = 4;
        classes_c.gridy = 0;
        classes_c.fill = GridBagConstraints.NONE;
        classes_gridbag.setConstraints (m_listClassBtn, classes_c);
        panel_allClasses.add (m_listClassBtn);
        
        m_createClassBtn = new JButton ("Create Class");
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.gridx = 4;
        classes_c.gridy = 1;
        classes_c.fill = GridBagConstraints.NONE;
        classes_gridbag.setConstraints (m_createClassBtn, classes_c);
        panel_allClasses.add (m_createClassBtn);
        
        m_deleteClassBtn = new JButton ("Delete Class");
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.gridx = 4;
        classes_c.gridy = 2;
        classes_c.fill = GridBagConstraints.NONE;
        classes_gridbag.setConstraints (m_deleteClassBtn, classes_c);
        panel_allClasses.add (m_deleteClassBtn);
        
        m_joinClassBtn = new JButton ("Join Class");
        classes_c.gridwidth = 1;                //reset to the default
        classes_c.gridheight = 1;
        classes_c.weighty = 1.0;
        classes_c.gridx = 4;
        classes_c.gridy = 3;
        classes_c.fill = GridBagConstraints.NONE;
        classes_gridbag.setConstraints (m_joinClassBtn, classes_c);
        panel_allClasses.add (m_joinClassBtn);
         
         // in-class panel
        JPanel panel_inClass = new JPanel ();
        m_tabPan.add ("in class", panel_inClass);
    
        GridBagLayout inClasses_gridbag = new GridBagLayout();
        GridBagConstraints inClasses_c = new GridBagConstraints();
        panel_inClass.setLayout (inClasses_gridbag);
        
        // process all class panels
        m_studentList = new JList<String> (m_peopleInClass);
        m_studentList.setSelectionMode (ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        m_studentList.setLayoutOrientation (JList.HORIZONTAL_WRAP);
        m_studentList.setVisibleRowCount (-1);
        inClasses_c.gridwidth = 3;                
        inClasses_c.gridheight = 5;
        inClasses_c.weighty = 1.0;
        inClasses_c.weightx = 1.0;
        inClasses_c.gridx = 0;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.BOTH;
        inClasses_gridbag.setConstraints (m_studentList, inClasses_c);
        panel_inClass.add (m_studentList);
        
        m_kickStudentBtn = new JButton ("Kick Student");
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 1.0;
        inClasses_c.gridx = 4;
        inClasses_c.gridy = 0;
        inClasses_c.fill = GridBagConstraints.NONE;
        inClasses_gridbag.setConstraints (m_kickStudentBtn, inClasses_c);
        panel_inClass.add (m_kickStudentBtn);
        
        m_queryClassBtn = new JButton ("Query class");
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 1.0;
        inClasses_c.gridx = 4;
        inClasses_c.gridy = 1;
        inClasses_c.fill = GridBagConstraints.NONE;
        inClasses_gridbag.setConstraints (m_queryClassBtn, inClasses_c);
        panel_inClass.add (m_queryClassBtn);
        
        m_leaveClassBtn = new JButton ("Leave class");
        inClasses_c.gridwidth = 1;                
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 1.0;
        inClasses_c.gridx = 4;
        inClasses_c.gridy = 2;
        inClasses_c.fill = GridBagConstraints.NONE;
        inClasses_gridbag.setConstraints (m_leaveClassBtn, inClasses_c);
        panel_inClass.add (m_leaveClassBtn);
        
        m_reqPresenterBtn = new JButton ("Request Presenter");
        inClasses_c.gridwidth = 1;           
        inClasses_c.gridheight = 1;
        inClasses_c.weighty = 1.0;
        inClasses_c.gridx = 4;
        inClasses_c.gridy = 3;
        inClasses_c.fill = GridBagConstraints.NONE;
        inClasses_gridbag.setConstraints (m_reqPresenterBtn, inClasses_c);
        panel_inClass.add (m_reqPresenterBtn);
        
        // Finalize UI config
        setSize (640, 480);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setTitle ("Cloud Classroom Control Panel");
        pack ();
        setLocationRelativeTo (null);
        setVisible (true);
    }
    
    public void actionPerformed (ActionEvent e) {
        
        WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
        WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
        Object src = e.getSource (); 
        int cookieId = repo.getCookieId ();
        
        if (src == m_logoutBtn) {
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr ("LOGOUT_REQ");
            cmd.pushStr (":" + cookieId);
            cmd.pushStr ("END");
            ntfServ.sendMsgToServer (cmd);
            System.exit (0);
        } else if (src == m_listClassBtn) {
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr ("LIST_CLASS_REQ");
            cmd.pushStr (":" + cookieId);
            cmd.pushStr ("END");
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
                cmd.pushStr ("CREATE_CLASS_REQ");
                cmd.pushStr (":temp_class_name"); //TOD: to fix
                cmd.pushStr (":" + cookieId);
                cmd.pushStr ("END");
                ntfServ.sendMsgToServer (cmd);
                return;
            } else {
                // bad input
            }
        } else if (src == m_deleteClassBtn) {
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr ("DEL_CLASS_REQ");
            cmd.pushStr (":" + cookieId);
            cmd.pushStr (":123"); //TOD: to fix
            cmd.pushStr ("END");
            ntfServ.sendMsgToServer (cmd);
        } else if (src == m_joinClassBtn) {
            WinServ_ReqCommand cmd = new WinServ_ReqCommand ();
            cmd.pushStr ("JOIN_CLASS_REQ");
            cmd.pushStr (":123"); //TOD: to fix
            cmd.pushStr (":" + cookieId);
            cmd.pushStr ("END");
            ntfServ.sendMsgToServer (cmd);
        } else if (src == m_kickStudentBtn) {
            // TODO
            int student_idx = m_studentList.getSelectedIndex ();
            
        } else if (src == m_queryClassBtn) {
        } else if (src == m_leaveClassBtn) {
        } else if (src == m_reqPresenterBtn) {
        }
    }
}