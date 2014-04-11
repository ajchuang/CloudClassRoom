import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class WinServ_NotificationWriter implements Runnable {

    PrintWriter m_writer;
    LinkedBlockingQueue<WinServ_ReqCommand> m_queue;

    public WinServ_NotificationWriter (PrintWriter wtr) {
        m_writer = wtr;
        m_queue = new LinkedBlockingQueue<WinServ_ReqCommand> ();
    }

    public void run () {
        
        while (true) {
        }
    }

}