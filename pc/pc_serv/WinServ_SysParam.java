import java.net.*;
import java.nio.charset.Charset;

public class WinServ_SysParam {
    
    public static final int M_IMG_VIEW_PORT = 8001;
    public static final int M_TXT_VIEW_PORT = 8002;
    public static final int M_CTL_VIEW_PORT = 8003;
    public static final int M_TEST_PORT     = 5566;
    public static final int M_DEF_SVR_PORT  = 4119;
    
    public static final String M_LOCALHOST  = "localhost";
    public static final String M_BKT_NAME   = "CloudClassRoom";
    public static final String M_ROOT_FS    = "./fs/";
    
    public static String getFsPath (String fileName) {
        return M_ROOT_FS + fileName;
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