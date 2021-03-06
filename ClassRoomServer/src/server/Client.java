package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Socket s;
		PrintWriter out;
		BufferedReader serverInputStream;
		final String ipAddress = "localhost";
		final int port = 4119;
		try {
			s = new Socket(ipAddress, port);
			final OutputStream outStream = s.getOutputStream();
			out = new PrintWriter(outStream, true);
			final InputStream inStream = s.getInputStream();
			serverInputStream = new BufferedReader(new InputStreamReader(
					inStream));
			out.println("LOGIN_REQ\n:Rui Chen\n:rc2639\n:iOS\n:sss\nEND");
			out.close();
			Thread.sleep(10000);
			s.close();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
