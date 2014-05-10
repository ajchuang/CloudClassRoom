/*
package server;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.*;
import javapns.notification.*;

import java.util.*;

public class PushNotification {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			List<PushedNotification> rsp = Push.alert("Hello World!", "Cloud_Classroom.p12", "123qweasdzxcv", false, "dd6f6c4209b7bbef85a58de807640f9fd9771f11594a9de2e2b17d1bd22e76ea");
			
			for (PushedNotification pnf: rsp) {
				if (pnf.isSuccessful ()) {
					System.out.println ("Oh yeah!");
				} else {
					String tok = pnf.getDevice().getToken ();
					ResponsePacket theError = pnf.getResponse ();
					
					System.out.println ("Oh no! : " + tok);
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
*/
