
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class WinServ_LoginWindow extends JFrame implements ActionListener {

    // Logic members
    boolean m_isAutheticating = false;

    // UI components
    JLabel  m_nameLabel;
    JLabel  m_pwdLabel;
    JTextField m_nameText;
    JPasswordField m_pwdText;
    JButton m_loginBtn;
    JButton m_cancelBtn;

    public WinServ_LoginWindow () {

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
        
        m_loginBtn = new JButton (new String ("Login"));
        m_loginBtn.addActionListener (this);
        GridBagConstraints c5 = new GridBagConstraints ();
        c5.gridx = 4;
        c5.gridy = 2;
        c5.gridwidth = 1;
        c5.gridheight = 1;
        c5.fill = GridBagConstraints.NONE;
        c5.anchor = GridBagConstraints.CENTER;
        add (m_loginBtn, c5);

        pack ();
        setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
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
                
                // send msg to server
                sendLoginMsg (name, pwd);
                
                dispose ();
                
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
    
    void sendLoginMsg (String name, String pass) {
        
        try {
            
            Socket s = new Socket ("localhost", WinServ.getPort ());
            
            PrintWriter writer = new PrintWriter (s.getOutputStream (), true);
            writer.println ("LOGIN_REQ");
            writer.println (Integer.toString (name.length ()));        
            writer.println (name);
            writer.println (Integer.toString (pass.length ()));        
            writer.println (pass);
            writer.println ("END");
            s.close ();
            
        } catch (Exception e) {
            e.printStackTrace ();
            System.exit (0);
        }        
        
        
    }
}