import java.net.*;
import java.io.File;
import java.nio.charset.Charset;

public class WinServ_SysParam {
    
    public static final int M_IMG_VIEW_PORT = 8001;
    public static final int M_TXT_VIEW_PORT = 8002;
    public static final int M_CTL_VIEW_PORT = 8003;
    public static final int M_TEST_PORT     = 5566;
    public static final int M_DEF_SVR_PORT  = 4119;
    
    public static final String M_LOCALHOST  = "localhost";
    public static final String M_BKT_NAME   = "CloudClassRoom";
    public static final String M_ROOT_FS    = "fs";
    public static final String M_SYS_FS     = "sys";
    public static final String M_RES_FS     = "res";
    
    public static final String gtResPath (String res) {
        String path = 
            "." + fsSeparator () + M_RES_FS + 
                  fsSeparator () + res; 
        WinServ.logInfo ("Res path: " + path);
        return path;
    }
    
    public static String getFsPath (String fileName) {
        String path = 
            "." + fsSeparator () + M_ROOT_FS + 
                  fsSeparator () + fileName; 
        WinServ.logInfo ("File path: " + path);
        return path;
    }
    
    public static String getSecretFileS3 () {
        String path = 
            "." + fsSeparator () + M_SYS_FS + 
                  fsSeparator () + "AwsCredentials.properties";
        WinServ.logInfo ("S3 secret: " + path);
        return path;
    }
    
    public static String getPptxToolPath () {
        String path = 
            "." + fsSeparator () + M_SYS_FS + 
                  fsSeparator () + "CloudClassRoom.exe";
        WinServ.logInfo ("S3 secret: " + path);
        return path;
    }
    
    public static String fsSeparator () {
        return File.	separator;
    } 
    
    public static boolean isImageFile (String fname) {
        
        String lower = fname.toLowerCase ();
        
        if (fname.endsWith (".jpg")     ||
            fname.endsWith (".jpeg")    ||
            fname.endsWith (".png")) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isWindows () {
        
        String OS = System.getProperty("os.name").toLowerCase();
        
        if (OS.indexOf ("win") >= 0)
            return true;
        else
            return false;
    }
    
    public static void sendMsg (String msgType, String msg, int port) throws Exception {
        
        String finalMsg = msgType + ":" + msg;
        byte buffer[] = finalMsg.getBytes (Charset.forName ("UTF-8"));
        
        // create data packe
        DatagramPacket packet = 
            new DatagramPacket (
                buffer, 
                buffer.length, 
                InetAddress.getByName (M_LOCALHOST), 
                port);
        
        // send the packet
        DatagramSocket socket = new DatagramSocket ();   
        socket.send (packet);                            
        socket.close ();                                 
    }
}