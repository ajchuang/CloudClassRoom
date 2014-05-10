package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushedNotification;
import javapns.notification.ResponsePacket;

import server.ServerModel.MessageToClient;

import message.ChangePresentTokenResMsg;
import message.CreateClassReqMsg;
import message.CreateUsrReqMsg;
import message.DeleteClassReqMsg;
import message.GetPresentTokenReqMsg;
import message.JoinClassApprovalResMsg;
import message.JoinClassReqMsg;
import message.KickUserReqMsg;
import message.ListClassReqMsg;
import message.LoginReqMsg;
import message.LoginResultMsg;
import message.LogoutReqMsg;
import message.Message;
import message.MessageFactory;
import message.PushContentReqMsg;
import message.QueryClassInfoReqMsg;
import message.QueryLatestContentReqMsg;
import message.QuitClassReqMsg;
import message.RetrivePresentTokenReqMsg;
import message.UnknownMessageException;

public class ServerThread implements Runnable {
	private final Socket incoming;
	private final ServerModel server;
	private String userName = null;
	private boolean firstLoginMsg;

	public ServerThread(final Socket s, final ServerModel server) {
		incoming = s;
		this.server = server;
		firstLoginMsg = true;
	}

	private void sendMessages(final Message msg, final PrintWriter out) {
		System.out.println("Sending " + msg.toMseeage());
		out.println(msg.toMseeage());
		//out.println (msg.toMseeage ().getBytes (Charset.forName ("UTF-8")));
	}

	private void sendMessages(final Collection<Message> messages,
			final PrintWriter out) {
		for (final Message msg : messages) {
			sendMessages(msg, out);
		}
	}

	private void sendMessageOrNotification(
			final Collection<MessageToClient> messages) {
		try {
			for (final MessageToClient output : messages) {
				if (output.socket != null) {
					sendMessages(output.messagesToSend, new PrintWriter(
							output.socket.getOutputStream(), true));
				} else if (output.user != null) {
					// TODO Push notification
					System.out.println("push notification to "
							+ output.user.getUser().getUserName());
					try {
						List<PushedNotification> rsp = Push.alert(output.messagesToSend.toMseeage(), "Cloud_Classroom.p12", "123qweasdzxcv", false, output.user.getTokenId());
						
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void run() {
		System.out.println("Started a new server thread");
		try {
			// should always ask for user name and password
			final OutputStream outStream = incoming.getOutputStream();
			PrintWriter out = new PrintWriter(outStream, true);
			final InputStream inStream = incoming.getInputStream();
			final BufferedReader in = new BufferedReader(new InputStreamReader(
					inStream));
			StringBuilder pendingMessage = new StringBuilder();
			String msgFromClient;
			while (true) {
				try {
					msgFromClient = in.readLine();
					if (msgFromClient == null) {
						break;
					}
					System.out.println("Input from client " + msgFromClient);
					if (!Message.END.equals(msgFromClient)) {
						if (pendingMessage.toString().isEmpty()) {
							pendingMessage.append(msgFromClient);
						} else {
							pendingMessage.append(Message.SEPARATOR
									+ msgFromClient);
						}
						continue;
					}
					final String message = pendingMessage.toString();
					System.out.println("parsing message " + message);
					pendingMessage = new StringBuilder();
					// process client message according to client state and the
					// message
					final Message messageFromClient = MessageFactory
							.parse(message);
					if (messageFromClient instanceof LoginReqMsg) {
						userName = ((LoginReqMsg) messageFromClient)
								.getUserName();
						sendMessages(server.login(
								(LoginReqMsg) messageFromClient, incoming), out);
					} else if (messageFromClient instanceof CreateUsrReqMsg) {
						sendMessages(
								server.createUser((CreateUsrReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof LogoutReqMsg) {
						sendMessages(
								server.logout((LogoutReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof CreateClassReqMsg) {
						sendMessages(
								server.createClass((CreateClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof ListClassReqMsg) {
						sendMessages(
								server.listClass((ListClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof DeleteClassReqMsg) {
						sendMessages(
								server.deleteClass((DeleteClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof JoinClassReqMsg) {
						final List<MessageToClient> outputs = server
								.joinClassRequest(incoming,
										(JoinClassReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof JoinClassApprovalResMsg) {
						final List<MessageToClient> outputs = server
								.joinClassResult(
										incoming,
										(JoinClassApprovalResMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof QueryClassInfoReqMsg) {
						sendMessages(
								server.queryClassInfo((QueryClassInfoReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof QuitClassReqMsg) {
						sendMessages(
								server.quitClass((QuitClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof KickUserReqMsg) {
						final List<MessageToClient> outputs = server
								.kickUserFromClass(incoming,
										(KickUserReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof PushContentReqMsg) {
						final List<MessageToClient> outputs = server
								.pushContent(incoming,
										(PushContentReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
						// } else if (messageFromClient instanceof
						// PushContentGetReqMsg) {
						// sendMessages(
						// server.getContent((PushContentGetReqMsg)
						// messageFromClient),
						// out);
					} else if (messageFromClient instanceof GetPresentTokenReqMsg) {
						final List<MessageToClient> outputs = server
								.getPresenterRequest(
										incoming,
										(GetPresentTokenReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof ChangePresentTokenResMsg) {
						final List<MessageToClient> outputs = server
								.changePresentResult(
										incoming,
										(ChangePresentTokenResMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof RetrivePresentTokenReqMsg) {
						final List<MessageToClient> outputs = server
								.retrivePresentToken(
										incoming,
										(RetrivePresentTokenReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof QueryLatestContentReqMsg) {
						sendMessages(
								server.queryLatestContent((QueryLatestContentReqMsg) messageFromClient),
								out);
					}
				} catch (final UnknownMessageException e) {
					System.out.println("Unknown message ");
				}
			}
		} catch (final SocketException e) {
			System.out.println("Client closed socket");
			if (userName != null) {
				server.suspendClientSession(userName);
				System.out.println("Client session suspended: " + userName);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.println(e);
			// throw new RuntimeException(e);
		} finally {

		}
	}
}
