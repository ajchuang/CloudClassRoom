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
	private long cookieId = -1;

	public ServerThread(final Socket s, final ServerModel server) {
		incoming = s;
		this.server = server;
	}

	private void sendMessages(final Message msg, final PrintWriter out) {
		System.out.println("Sending " + msg.toMseeage());
		out.println(msg.toMseeage());
		// out.println (msg.toMseeage ().getBytes (Charset.forName ("UTF-8")));
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
						List<PushedNotification> rsp = Push.alert(
								output.messagesToSend.toMseeage(),
								"dev_id.p12", "123qweasdzxcv", false,
								output.user.getTokenId());

						for (PushedNotification pnf : rsp) {
							if (pnf.isSuccessful()) {
								System.out.println("Oh yeah!");
							} else {
								String tok = pnf.getDevice().getToken();
								ResponsePacket theError = pnf.getResponse();

								System.out.println("Oh no! : " + tok);
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
			String msgFromClient = null;
			StringBuilder bdr;
			// @lfred
			int data;

			while (true) {
				try {
					// try {
					// String tmp = new String ();
					bdr = new StringBuilder();
					while (true) {
						data = in.read();
						if (data >= 0) {

							if (data == 0x0a) {
								msgFromClient = bdr.toString();
								break;
							} else if (data == 0x0d) {
								continue;
							}

							bdr.append((char) data);
						} else {
							System.out.println("FIN received");
							throw new SocketException();
						}
					}

					// msgFromClient = in.readLine();
					// } catch (Exception xxx) {
					// System.out.println (xxx);
					// xxx.printStackTrace ();
					// }

					if (msgFromClient == null) {
						break;
					}
					System.out.println("Input from client :" + msgFromClient);
					// System.out.println(msgFromClient.length());
					// System.out.println(Message.END.length());
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
						userName = ((CreateUsrReqMsg) messageFromClient)
								.getUserName();
						sendMessages(
								server.createUser((CreateUsrReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof LogoutReqMsg) {
						cookieId = ((LogoutReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.logout((LogoutReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof CreateClassReqMsg) {
						cookieId = ((CreateClassReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.createClass((CreateClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof ListClassReqMsg) {
						cookieId = ((ListClassReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.listClass((ListClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof DeleteClassReqMsg) {
						cookieId = ((DeleteClassReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.deleteClass((DeleteClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof JoinClassReqMsg) {
						cookieId = ((JoinClassReqMsg) messageFromClient)
								.getCookieId();
						final List<MessageToClient> outputs = server
								.joinClassRequest(incoming,
										(JoinClassReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof JoinClassApprovalResMsg) {
						cookieId = ((JoinClassApprovalResMsg) messageFromClient)
								.getApproverCookieId();
						final List<MessageToClient> outputs = server
								.joinClassResult(
										incoming,
										(JoinClassApprovalResMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof QueryClassInfoReqMsg) {
						cookieId = ((QueryClassInfoReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.queryClassInfo((QueryClassInfoReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof QuitClassReqMsg) {
						cookieId = ((QuitClassReqMsg) messageFromClient)
								.getCookieId();
						sendMessages(
								server.quitClass((QuitClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof KickUserReqMsg) {
						cookieId = ((KickUserReqMsg) messageFromClient)
								.getCookieId();
						final List<MessageToClient> outputs = server
								.kickUserFromClass(incoming,
										(KickUserReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof PushContentReqMsg) {
						cookieId = ((PushContentReqMsg) messageFromClient)
								.getCookieId();
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
						cookieId = ((GetPresentTokenReqMsg) messageFromClient)
								.getCookieId();
						final List<MessageToClient> outputs = server
								.getPresenterRequest(
										incoming,
										(GetPresentTokenReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof ChangePresentTokenResMsg) {
						cookieId = ((ChangePresentTokenResMsg) messageFromClient)
								.getApproverCookieId();
						final List<MessageToClient> outputs = server
								.changePresentResult(
										incoming,
										(ChangePresentTokenResMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof RetrivePresentTokenReqMsg) {
						cookieId = ((RetrivePresentTokenReqMsg) messageFromClient)
								.getCookieId();
						final List<MessageToClient> outputs = server
								.retrivePresentToken(
										incoming,
										(RetrivePresentTokenReqMsg) messageFromClient);
						sendMessageOrNotification(outputs);
					} else if (messageFromClient instanceof QueryLatestContentReqMsg) {
						cookieId = ((QueryLatestContentReqMsg) messageFromClient)
								.getCookieId();
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
			if (cookieId != -1) {
				System.out.println("Get username from cookie id " + cookieId);
				userName = server.getUserFromCookieId(cookieId);
			}
			if (userName != null) {
				server.suspendClientSession(userName);
				System.out.println("Client session suspended: " + userName);
			}

			// @lfred
			try {
				incoming.close();
			} catch (Exception eeeee) {
				System.out.println("Oh NO");
				eeeee.printStackTrace();

			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.println(e);
			// throw new RuntimeException(e);
		} /*
		 * finally { System.out.println ("Finally -@lfred"); }
		 */
	}
}
