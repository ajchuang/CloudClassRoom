// @lfred:
// The original version of the source codes comes from 
// the tutorial website: http://www.sourcecodester.com/java/5752/simple-image-viewer-java.html
// Although it is not written in the line 0, but we have modified this file a lot.
// 1. Add Socket-IPC capability
// 2. Make it a slave app of the CloudClassRoom local server
// 3. Renovate the whole UI.
// We simple use the part of the UI of the old app, and the internal logics are also renovated.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import pc_common.*;

public class PC_TinyImageViewer extends JFrame implements ActionListener, PC_SimpleMsgHandler {
	
    // UI components
    JTabbedPane m_tabPan;
	JButton m_openBtn;
    JButton m_shareBtn;
    JButton m_saveBtn;
    
    PC_ImagePanel m_localPanel;
    PC_ImagePanel m_remotePanel;
    
    String m_currentLocalFile;
    String m_currentRemoteFile;
    
    public static void main (String[] args) {
		
		PC_TinyImageViewer img = new PC_TinyImageViewer ();
		
		img.setSize (800, 600);
		img.setVisible (true);
		img.setDefaultCloseOperation (EXIT_ON_CLOSE);
		img.setResizable (false);
	}
    
	public PC_TinyImageViewer () {
        
        super ("Simple Image Viewer");
        
        setupUiComponents ();
        PC_SimpleReceiver.startReceiver (8001, this);
	}
    
    void setupUiComponents () {
        
        setLayout (new BorderLayout ());
        
        // Create buttons
        m_openBtn = new JButton (new ImageIcon ("res/open.png"));
        m_shareBtn = new JButton (new ImageIcon ("res/share.png"));
        m_saveBtn = new JButton (new ImageIcon ("res/save.png"));
        
        m_shareBtn.addActionListener (this);
        m_openBtn.addActionListener (this);
        m_saveBtn.addActionListener (this);
        
        // process the Toolbar
        JToolBar toolBar = new JToolBar ();
        toolBar.setMargin (new Insets (2, 2, 2, 2));
        toolBar.setBorderPainted (true);
        toolBar.setFloatable (false);
    
        toolBar.add (m_openBtn);
        //toolBar.add (m_saveBtn);
        toolBar.addSeparator ();
        toolBar.add (m_shareBtn);
        toolBar.addSeparator ();
        add (toolBar, BorderLayout.NORTH);
        
        // create 2 panels and add to TABBED panel
        m_localPanel = new PC_ImagePanel ();
        m_remotePanel = new PC_ImagePanel ();
        
        m_tabPan = new JTabbedPane (JTabbedPane.TOP);
        m_tabPan.add ("Local", m_localPanel);
        m_tabPan.add ("Remote", m_remotePanel);
    
        add (m_tabPan, BorderLayout.CENTER);
    }
    
    public void actionPerformed (ActionEvent e) {
        
        if (e.getSource () == m_openBtn) {
            JFileChooser fileChooser = new JFileChooser ();
            int retval = fileChooser.showOpenDialog (this);
            
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = fileChooser.getSelectedFile ();
                m_currentLocalFile = f.getAbsolutePath ();
                m_localPanel.drawNewFile (m_currentLocalFile);
            }
        } else if (e.getSource () == m_shareBtn) {
            
            if (m_currentLocalFile == null) {
                JOptionPane.showMessageDialog (
                    null, "Not file selected", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                sharingImageFile ();
            }
        } else if (e.getSource () == m_saveBtn) {
        }
    }
    
    void sharingImageFile () {
        try {
            Socket sck = new Socket ("localhost", 5566);
            PrintWriter writer = new PrintWriter (sck.getOutputStream (), true);
            writer.println ("UPDATE_FILE");
            writer.println (Integer.toString (m_currentLocalFile.length ()) + ":" + m_currentLocalFile);
            writer.println ("END");
            sck.close ();
        } catch (Exception ee) {
            ee.printStackTrace ();
        }
    }
    
    @Override
    public void simpleMsgHandler (String msg) {
        System.out.println (msg);
        
        // We handle: UPDATE:<path>
        if (msg.startsWith ("UPDATE:")) {
            String path = msg.substring (7);
            m_remotePanel.drawNewFile (path);
        }
    } 
	
    // inner class used to control the 
	private class PC_ImagePanel extends JPanel  {
		
        Color m_chalkBoardGreen = new Color (59, 101, 61);
        Image m_image;
        
        public void drawNewFile (String name) {
            m_image = new ImageIcon (name).getImage ();
            repaint ();
        }
                
        public void paintComponent (Graphics g) {
            	
            if (m_image != null) {
                g.setColor (Color.WHITE);
                g.fillRect (0, 0, 800, 600);
                
                int w = m_image.getWidth (null);
                int h = m_image.getHeight (null);
                int r = scaleRatio (w, h);    
                    
                g.drawImage (m_image, 0, 0, w/r, h/r, null);
            }
        }
        
        int scaleRatio (int w, int h) {
            
            int r1 = 1, r2 = 1;
            
            if (w > getWidth ())
                r1 = w/getWidth () + 1;
                
            if (h > getHeight ())
                r2 = h/getHeight () + 1;
                    
            return (r1 > r2) ? r1:r2;
        }
	}
}

