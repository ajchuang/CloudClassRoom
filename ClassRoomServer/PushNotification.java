//*
//package server;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.*;
import javapns.notification.*;

import java.util.*;

public class PushNotification {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println ("Aloha");
	        org.apache.log4j.BasicConfigurator.configure ();				
		try {
			List<PushedNotification> rsp = 
				Push.alert (
					"Hello World!", 
					"dev_id.p12", 
					"123qweasdzxcv", 
					false, 
					"ace013e9587b0e88dd7fae468f4846043b1c7c8a899fa120f939c41100d5f605");
			
			for (PushedNotification pnf: rsp) {

				if (pnf.isTransmissionCompleted ()) 
					System.out.println ("Trans completed");
				else
					System.out.println ("Trans NOT completed");

				if (pnf.isSuccessful ()) {
					System.out.println ("Oh yeah!");
				} else {
					String tok = pnf.getDevice().getToken ();
					ResponsePacket theError = pnf.getResponse ();
					
					System.out.println ("No! tries: " + pnf.getLatestTransmissionAttempt() + "tok: " + tok);
					System.out.println ("Why:" + theError.getMessage ());
				}
			}
			
			
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeystoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
//*/
