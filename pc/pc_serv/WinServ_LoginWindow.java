
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class WinServ_LoginWindow extends JFrame implements ActionListener, WinServ_MsgHandler {

    // Logic members
    boolean m_isAutheticating = false;

    // UI components
    JLabel  m_nameLabel;
    JLabel  m_pwdLabel;
    JTextField m_nameText;
    JPasswordField m_pwdText;
    JButton m_loginBtn;
    JButton m_cancelBtn;
    
    JCheckBox m_signUp;
    JCheckBox m_inst;

    public WinServ_LoginWindow () {
        
        // config UI
        setLayout (new GridBagLayout ());

        m_nameLabel = new JLabel (new String ("Name: "));
        GridBagConstraints c1 = new GridBagConstraints ();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.fill = GridBagConstraints.NONE;
        c1.anchor = GridBagConstraints.CENTER;
        add (m_nameLabel, c1);
        
        m_nameText = new JTextField (12);
        GridBagConstraints c2 = new GridBagConstraints ();
        c2.gridx = 1;
        c2.gridy = 0;
        c2.gridwidth = 5;
        c2.gridheight = 1;
        c2.fill = GridBagConstraints.NONE;
        c2.anchor = GridBagConstraints.CENTER;
        add (m_nameText, c2);
        
        m_pwdLabel = new JLabel (new String ("Password: "));
        GridBagConstraints c3 = new GridBagConstraints ();
        c3.gridx = 0;
        c3.gridy = 1;
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.fill = GridBagConstraints.NONE;
        c3.anchor = GridBagConstraints.CENTER;
        add (m_pwdLabel, c3);
        
        m_pwdText = new JPasswordField (12);
        GridBagConstraints c4 = new GridBagConstraints ();
        c4.gridx = 1;
        c4.gridy = 1;
        c4.gridwidth = 5;
        c4.gridheight = 1;
        c4.fill = GridBagConstraints.NONE;
        c4.anchor = GridBagConstraints.CENTER;
        add (m_pwdText, c4);
        
        m_cancelBtn = new JButton (new String ("Cancel"));
        m_cancelBtn.addActionListener (this);
        GridBagConstraints c6 = new GridBagConstraints ();
        c6.gridx = 2;
        c6.gridy = 2;
        c6.gridwidth = 1;
        c6.gridheight = 1;
        c6.fill = GridBagConstraints.NONE;
        c6.anchor = GridBagConstraints.CENTER;
        add (m_cancelBtn, c6);
        
        m_loginBtn = new JButton (new String ("Ok"));
        m_loginBtn.addActionListener (this);
        GridBagConstraints c5 = new GridBagConstraints ();
        c5.gridx = 4;
        c5.gridy = 2;
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.fill = GridBagConstraints.NONE;
        c5.anchor = GridBagConstraints.CENTER;
        add (m_loginBtn, c5);
        
        m_signUp = new JCheckBox ("Sing Up");
        GridBagConstraints c7 = new GridBagConstraints ();
        c7.gridx = 2;
        c7.gridy = 3;
        c7.gridwidth = 1;
        c7.gridheight = 1;
        c7.fill = GridBagConstraints.NONE;
        c7.anchor = GridBagConstraints.CENTER;
        add (m_signUp, c7);
        
        m_inst = new JCheckBox ("Instructor");
        GridBagConstraints c8 = new GridBagConstraints ();
        c8.gridx = 4;
        c8.gridy = 3;
        c8.gridwidth = 1;
        c8.gridheight = 1;
        c8.fill = GridBagConstraints.NONE;
        c8.anchor = GridBagConstraints.CENTER;
        add (m_inst, c8);

        pack ();
        setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        setBounds (
            300, 200, 
            (int)getSize().getWidth() + 10, 
            (int)getSize().getHeight() + 10);
        setVisible (true);
    }
    
    public void actionPerformed (ActionEvent e) {

        if (e.getSource () == m_loginBtn) {
        
            if (m_isAutheticating == false) {
                System.out.println ("Login Button is performed.");

                String name = m_nameText.getText();
                String pwd = new String (m_pwdText.getPassword ());

                if (name.length () == 0 || pwd.length () == 0) {
                    System.out.println ("Empty Strings - no login");
                    return;
                }

                m_isAutheticating = true;
                m_loginBtn.setEnabled (false);
                m_cancelBtn.setEnabled (false);
                
                // to prevent the user change the user name all the time.
                m_nameText.setEditable (false);
                m_pwdText.setEditable (false);
                
                if (m_signUp.isSelected ()) {
                    sendSignUpMsg (name, pwd);
                } else {
                    sendLoginMsg (name, pwd);
                }
                
            } else {
            
                // block UI
                System.out.println ("Still authenticating...please wait");
            }

        } else if (e.getSource () == m_cancelBtn) {

            // TODO: shutdown the client
            System.out.println ("Cancel Button is performed.");
            System.exit (0);
        }
    }
    
    void sendSignUpMsg (String name, String pass) {
        
            WinServ_ReqCommand loginCmd = new WinServ_ReqCommand ();
            
            loginCmd.pushStr ("NEW_USER_REQ");
            loginCmd.pushStr (":" + name);
            loginCmd.pushStr (":" + pass);
            
            if (m_inst.isSelected ()) {
                loginCmd.pushStr (":Instructor");
            } else {
                loginCmd.pushStr (":Student");
            }
            
            loginCmd.pushStr ("END");
            
            // REGISTER NW receiver
            WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
            ntfServ.registerMsgHandler ("NEW_USER_RES", this);
            
            // send event
            ntfServ.sendMsgToServer (loginCmd);
    }
    
    void sendLoginMsg (String name, String pass) {
        
        try {
            
            WinServ_ReqCommand loginCmd = new WinServ_ReqCommand ();
            
            loginCmd.pushStr ("LOGIN_REQ");
            loginCmd.pushStr (":" + name);
            loginCmd.pushStr (":" + pass);
            loginCmd.pushStr (":PC");
            loginCmd.pushStr (":0");
            loginCmd.pushStr ("END");
            
            // REGISTER NW receiver
            WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
            ntfServ.registerMsgHandler ("LOGIN_RES", this);
            
            // send event
            ntfServ.sendMsgToServer (loginCmd);
            
        } catch (Exception e) {
            WinServ.logExp (e, true);
        }        
    }
    
    // @lfred: receiving server message
    public void handleServerMsg (WinServ_ReqCommand cmd) {
        
        WinServ.logInfo ("handleServerMsg @ login Window");
        m_isAutheticating = false;
        
        String ln1 = cmd.getStrAt (0);
        
        if (ln1.equals ("NEW_USER_RES")) {
            String ln2 = cmd.getStrAt (1).substring (1);
            
            if (ln2.equals ("SUCCESS")) {
                String name = m_nameText.getText();
                String pwd = new String (m_pwdText.getPassword ());
                sendLoginMsg (name, pwd);
            } else {
                JOptionPane.showMessageDialog (
                    this,
                    "Sign-up failed. Try again. Reason: " + ln2,
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    new ImageIcon (WinServ_SysParam.gtResPath ("error.png")));
                    
                // re-enable the login box
                m_loginBtn.setEnabled   (true);
                m_cancelBtn.setEnabled  (true);
                m_nameText.setEditable  (true);
                m_pwdText.setEditable   (true);
            }
        } else if (ln1.equals ("LOGIN_RES")) {
            String ln2 = cmd.getStrAt (1).substring (1);
            String ln3 = cmd.getStrAt (2).substring (1);
            String ln4 = cmd.getStrAt (3).substring (1);
        
        
            if (ln2.equals ("LOGGED_IN") || ln2.equals ("DUPLICATE")) {
                int cookieId = Integer.parseInt (ln3);
                
                // update repo
                WinServ_DataRepo repo = WinServ_DataRepo.getDataRepo ();
                repo.setLoggedIn (true);
                repo.setCookieId (cookieId);
                
                if (ln4.equals ("Instructor")) {
                    WinServ.logInfo ("I am inst");
                    repo.setInstructor (true);
                    repo.setPresenter (true);
                } else {
                    WinServ.logInfo ("I am student");
                    repo.setInstructor (false);
                    repo.setPresenter (false);
                }
                
                // clear the message registration
                WinServ_NtfServer ntfServ = WinServ_NtfServer.getNtfServ ();
                ntfServ.unregisterMsgHandler ("LOGIN_RES", this);
            
                // create new window, and close the login window
                WinServ_ControlPanel ctrlPanel = WinServ_ControlPanel.getCtrlPanel ();
                dispose ();
                
            } else {
                // error information
                WinServ.logInfo ("Login failed: " + ln2);
                
                JOptionPane.showMessageDialog (
                    this,
                    "Login failed. Try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE,
                    new ImageIcon (WinServ_SysParam.gtResPath ("error.png")));
                
                // re-enable the login box
                m_loginBtn.setEnabled   (true);
                m_cancelBtn.setEnabled  (true);
                m_nameText.setEditable  (true);
                m_pwdText.setEditable   (true);
            }
        } else {
            WinServ.logErr ("Bad response - " + ln1);
            System.exit (0);
        }
    
        
        
        
        
        
        // if login OKAY,
        // REGISTER NW receiver
        
    }
}