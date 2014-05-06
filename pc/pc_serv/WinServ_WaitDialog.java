import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class WinServ_WaitDialog extends JFrame {
    
    public static WinServ_WaitDialog sm_dialog;
    JLabel  m_label;
    
    static public WinServ_WaitDialog dialogFactory (Component parent, String slogan) {
        
        if (sm_dialog != null)
            return sm_dialog;
            
        sm_dialog = new WinServ_WaitDialog (slogan);
        sm_dialog.setLocationRelativeTo (parent);
        return sm_dialog;
    }
    
    static public void closeDialog () {
        if (sm_dialog != null)
            sm_dialog.dispose ();
            
        sm_dialog = null;
    }
    
    private WinServ_WaitDialog (String label) {
        
        // config UI
        setLayout (new BorderLayout ());
        
        m_label = new JLabel (label, new ImageIcon (WinServ_SysParam.gtResPath ("wait.png")), SwingConstants.CENTER);
    
        add (m_label, BorderLayout.CENTER);
        getContentPane().setSize (220, 180);
        pack ();
        setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
        setVisible (true);
    }

} 