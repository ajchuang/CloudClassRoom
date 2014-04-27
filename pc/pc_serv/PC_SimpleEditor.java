// Part of the code is from http://www.leepoint.net/notes-java/examples/components/editor/nutpad.html
// At this moment, we can only send the last line or the whole file.
// We modify the sample editor into an editor with sharing ability

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class PC_SimpleEditor extends JFrame implements KeyListener, ActionListener, PC_SimpleMsgHandler {
    
    // UI components
    private JTextArea    m_editArea;
    private JTextArea    m_shareArea;
    
    private JFileChooser m_fileChooser = new JFileChooser();
    
    JButton     m_openBtn;
    JButton     m_saveBtn;
    JButton     m_shareBtn;
    JTabbedPane m_tabPan;
    
    // Action objects
    private Action m_openAction  = new OpenAction ();
    private Action m_saveAction  = new SaveAction ();
    private Action m_shareAction = new ShareAction ();
    private Action m_exitAction  = new ExitAction ();
    
    static PC_SimpleEditor sm_editor = null;
    
    public static void startEditor () {
        
        if (sm_editor == null) {
            sm_editor = new PC_SimpleEditor ();
            sm_editor.addWindowListener (new java.awt.event.WindowAdapter() {
                public void windowClosing(WindowEvent winEvt) {
                    sm_editor = null;
                }
            });
        }
    }
    
    public PC_SimpleEditor () {
        setupUiComponent ();
        PC_SimpleReceiver.startReceiver (8002, this);
    }
    
    void setupUiComponent () {
        
        m_tabPan = new JTabbedPane (JTabbedPane.TOP);
        
        //... Create scrollable text area.
        m_editArea = new JTextArea (15, 80);
        m_editArea.setBorder (BorderFactory.createEmptyBorder(2,2,2,2));
        m_editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        m_editArea.addKeyListener (this);
        
        m_shareArea = new JTextArea (15, 80);
        m_shareArea.setBorder (BorderFactory.createEmptyBorder(2,2,2,2));
        m_shareArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        m_shareArea.setEditable (false);
        
        JScrollPane scrollingText1 = new JScrollPane (m_editArea);
        JScrollPane scrollingText2 = new JScrollPane (m_shareArea);
        m_tabPan.add ("Edit",  scrollingText1);
        m_tabPan.add ("Shared", scrollingText2);
        
        //-- Create a content pane, set layout, add component.
        JPanel content = new JPanel ();
        content.setLayout (new BorderLayout ());
        
        //... Create menubar
        JMenuBar menuBar = new JMenuBar ();
        JMenu fileMenu = menuBar.add (new JMenu ("File"));
        fileMenu.setMnemonic ('F');
        fileMenu.add (m_openAction);       // Note use of actions, not text.
        fileMenu.add (m_saveAction);
        fileMenu.addSeparator (); 
        
        fileMenu.add (m_shareAction);
        fileMenu.addSeparator (); 
        
        fileMenu.add (m_exitAction);
        
        //... Set window content and menu.
        setContentPane (content);
        setJMenuBar(menuBar);
        
        // add the table pane
        add (m_tabPan, BorderLayout.CENTER);
        
        // Toolbar config
        JToolBar toolBar = new JToolBar ();
        toolBar.setMargin (new Insets (2, 2, 2, 2));
        toolBar.setBorderPainted (true);
        toolBar.setFloatable (false);
        add (toolBar, BorderLayout.NORTH);
        
        m_saveBtn = new JButton (new ImageIcon ("res/save.png"));
        m_saveBtn.addActionListener (this);
        toolBar.add (m_saveBtn);
        
        m_openBtn = new JButton (new ImageIcon ("res/open.png"));
        m_openBtn.addActionListener (this);
        toolBar.add (m_openBtn);
        
        toolBar.addSeparator ();
        
        m_shareBtn = new JButton (new ImageIcon ("res/share.png"));
        m_shareBtn.addActionListener (this);
        toolBar.add (m_shareBtn);
        toolBar.addSeparator ();
        
        //... Set other window characteristics.
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setTitle ("PC_SimpleEditor");
        pack ();
        setLocationRelativeTo (null);
        setVisible (true);
    }
    
    class OpenAction extends AbstractAction {
        
        public OpenAction() {
            super("Open...");
            putValue(MNEMONIC_KEY, new Integer('O'));
        }
        
        public void actionPerformed(ActionEvent e) {
            int retval = m_fileChooser.showOpenDialog (PC_SimpleEditor.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = m_fileChooser.getSelectedFile ();
                try {
                    FileReader reader = new FileReader (f);
                    m_editArea.read (reader, "");  // Use TextComponent read
                } catch (IOException ioex) {
                    System.out.println (e);
                    System.exit (1);
                }
            }
        }
    }
    
    class SaveAction extends AbstractAction {
        
        SaveAction () {
            super ("Save...");
            putValue (MNEMONIC_KEY, new Integer('S'));
        }
        
        public void actionPerformed(ActionEvent e) {
            int retval = m_fileChooser.showSaveDialog (PC_SimpleEditor.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = m_fileChooser.getSelectedFile ();
                try {
                    FileWriter writer = new FileWriter (f);
                    m_editArea.write (writer);  // Use TextComponent write
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog (PC_SimpleEditor.this, ioex);
                    System.exit (1);
                }
            }
        }
    }
    
    class ExitAction extends AbstractAction {
        
        public ExitAction() {
            super("Exit");
            putValue(MNEMONIC_KEY, new Integer('X'));
        }
        
        public void actionPerformed (ActionEvent e) {
            System.exit (0);
        }
    }
    
    class ShareAction extends AbstractAction {
        
        public ShareAction() {
            super("Share");
        }
        
        public void actionPerformed (ActionEvent e) {
            
            System.out.println ("Sharing");
            
            try {
                File tempFile = File.createTempFile ("temp-edit-file", ".tmp");
                PrintWriter p = new PrintWriter (tempFile);
                String s = m_editArea.getText (); 
                p.print (s);
                p.close ();
                
                Socket sck = new Socket ("localhost", 5566);
                PrintWriter writer = new PrintWriter (sck.getOutputStream (), true);
                writer.println ("UPDATE_FILE");
                writer.println (Integer.toString (tempFile.getPath ().length ()) + ":" + tempFile.getPath ());
                writer.println ("END");
                sck.close ();
                
            } catch (Exception ee) {
                ee.printStackTrace ();
            }
        }
    }
    
    // just a bad way - perhaps need to refine
    public void actionPerformed (ActionEvent ae) {
        
        if (ae.getSource () == m_saveBtn) {
            m_saveAction.actionPerformed (null);
        } else if (ae.getSource () == m_openBtn) {
            m_openAction.actionPerformed (null);
        } else if (ae.getSource () == m_shareBtn) {
            m_shareAction.actionPerformed (null);
        }
    }
    
    // section: required by KeyListener
    public void keyPressed (KeyEvent e) {
    }

    public void keyTyped (KeyEvent e) {
    }

    public void keyReleased (KeyEvent e) {
        
        // Updating the last line
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            /*
            try {
                int lastLine = m_editArea.getLineCount () - 2;
                int sPos = m_editArea.getLineStartOffset (lastLine);
                int ePos = m_editArea.getLineEndOffset (lastLine);
                
                String content = m_editArea.getText ();                
                String send = content.substring (sPos, ePos - 1);
                
                System.out.println ("Sending: " + send);
                sendLine (send);
                
            } catch (Exception ee) {
                ee.printStackTrace ();
            }
            */
        }
    }
    
    @Override
    public void simpleMsgHandler (String msg) {
        
        // We handle: UPDATE:<path>
        if (msg.startsWith ("UPDATE:")) {
            String path = msg.substring (7);
            
            try {
                FileReader reader = new FileReader (path);
                m_shareArea.read (reader, "");  // Use TextComponent read
            } catch (IOException e) {
                e.printStackTrace ();
            }
        } 
    } 
    
    public void sendLine (String msg) {
        
        /* @lfred: I would like to disable the sending line feature - to leverage Amazon s3
        try {
            
            Socket s = new Socket ("localhost", WinServ.getPort ());
            
            PrintWriter writer = new PrintWriter (s.getOutputStream (), true);
            writer.println ("UPDATE_CODE");
            writer.println (Integer.toString (msg.length ()));        
            writer.println (msg);
            writer.println ("END");
            s.close ();
            
        } catch (Exception e) {
            e.printStackTrace ();
            System.exit (0);
        } 
        */       
    }
}