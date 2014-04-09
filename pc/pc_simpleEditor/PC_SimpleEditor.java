// Part of the code is from http://www.leepoint.net/notes-java/examples/components/editor/nutpad.html
// At this moment, we can only send the last line or the whole file.
// We modify the sample editor into an editor with sharing ability

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class PC_SimpleEditor extends JFrame implements KeyListener {
    
    // UI components
    private JTextArea    m_editArea;
    private JFileChooser m_fileChooser = new JFileChooser();
    
    // Action objects
    private Action m_openAction  = new OpenAction ();
    private Action m_saveAction  = new SaveAction ();
    private Action m_shareAction = new ShareAction ();
    private Action m_exitAction  = new ExitAction ();
     
    
    //===================================================================== main
    public static void main(String[] args) {
        new PC_SimpleEditor ();
    }
    
    //============================================================== constructor
    public PC_SimpleEditor () {
        //... Create scrollable text area.
        m_editArea = new JTextArea(15, 80);
        m_editArea.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        m_editArea.setFont(new Font("monospaced", Font.PLAIN, 14));
        m_editArea.addKeyListener (this);
        JScrollPane scrollingText = new JScrollPane(m_editArea);
        
        //-- Create a content pane, set layout, add component.
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.add(scrollingText, BorderLayout.CENTER);
        
        //... Create menubar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = menuBar.add(new JMenu("File"));
        fileMenu.setMnemonic('F');
        fileMenu.add (m_openAction);       // Note use of actions, not text.
        fileMenu.add (m_saveAction);
        fileMenu.addSeparator (); 
        
        fileMenu.add (m_shareAction);
        fileMenu.addSeparator (); 
        
        fileMenu.add (m_exitAction);
        
        //... Set window content and menu.
        setContentPane (content);
        setJMenuBar(menuBar);
        
        //... Set other window characteristics.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle ("PC_SimpleEditor");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    class OpenAction extends AbstractAction {
        
        public OpenAction() {
            super("Open...");
            putValue(MNEMONIC_KEY, new Integer('O'));
        }
        
        public void actionPerformed(ActionEvent e) {
            int retval = m_fileChooser.showOpenDialog(PC_SimpleEditor.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = m_fileChooser.getSelectedFile();
                try {
                    FileReader reader = new FileReader(f);
                    m_editArea.read(reader, "");  // Use TextComponent read
                } catch (IOException ioex) {
                    System.out.println(e);
                    System.exit(1);
                }
            }
        }
    }
    
    class SaveAction extends AbstractAction {
        
        SaveAction() {
            super("Save...");
            putValue(MNEMONIC_KEY, new Integer('S'));
        }
        
        public void actionPerformed(ActionEvent e) {
            int retval = m_fileChooser.showSaveDialog(PC_SimpleEditor.this);
            if (retval == JFileChooser.APPROVE_OPTION) {
                File f = m_fileChooser.getSelectedFile();
                try {
                    FileWriter writer = new FileWriter(f);
                    m_editArea.write(writer);  // Use TextComponent write
                } catch (IOException ioex) {
                    JOptionPane.showMessageDialog(PC_SimpleEditor.this, ioex);
                    System.exit(1);
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
                writer.println ("BEGIN UPDATE_FILE");
                writer.println (Integer.toString (tempFile.getPath ().length ()):tempFile.getPath ());
                writer.println ("END");
                sck.close ();
                
            } catch (Exception ee) {
                ee.printStackTrace ();
            }
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