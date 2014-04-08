package server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import server.ServerModel.SocketAndMessage;

import message.ChangePresentTokenResMsg;
import message.CreateClassReqMsg;
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
import message.PushContentGetReqMsg;
import message.PushContentReqMsg;
import message.QueryClassInfoReqMsg;
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
	}

	private void sendMessages(final Collection<Message> messages,
			final PrintWriter out) {
		for (final Message msg : messages) {
			sendMessages(msg, out);
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
			String msgFromClient;
			while (true) {
				try {
					msgFromClient = in.readLine();
					System.out.println("Received message " + msgFromClient);
					// process client message according to client state and the
					// message
					final Message messageFromClient = MessageFactory
							.parse(msgFromClient);
					if (messageFromClient instanceof LoginReqMsg) {
						sendMessages(server.login(
								(LoginReqMsg) messageFromClient, incoming), out);
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
						final List<SocketAndMessage> outputs = server
								.joinClassRequest(incoming,
										(JoinClassReqMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof JoinClassApprovalResMsg) {
						final List<SocketAndMessage> outputs = server
								.joinClassResult(
										incoming,
										(JoinClassApprovalResMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof QueryClassInfoReqMsg) {
						sendMessages(
								server.queryClassInfo((QueryClassInfoReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof QuitClassReqMsg) {
						sendMessages(
								server.quitClass((QuitClassReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof KickUserReqMsg) {
						final List<SocketAndMessage> outputs = server
								.kickUserFromClass(incoming,
										(KickUserReqMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof PushContentReqMsg) {
						final List<SocketAndMessage> outputs = server
								.pushContent(incoming,
										(PushContentReqMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof PushContentGetReqMsg) {
						sendMessages(
								server.getContent((PushContentGetReqMsg) messageFromClient),
								out);
					} else if (messageFromClient instanceof GetPresentTokenReqMsg) {
						final List<SocketAndMessage> outputs = server
								.getPresenterRequest(
										incoming,
										(GetPresentTokenReqMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof ChangePresentTokenResMsg) {
						final List<SocketAndMessage> outputs = server
								.changePresentResult(
										incoming,
										(ChangePresentTokenResMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					} else if (messageFromClient instanceof RetrivePresentTokenReqMsg) {
						final List<SocketAndMessage> outputs = server
								.retrivePresentToken(
										incoming,
										(RetrivePresentTokenReqMsg) messageFromClient);
						for (final SocketAndMessage output : outputs) {
							sendMessages(
									output.messagesToSend,
									new PrintWriter(output.socket
											.getOutputStream(), true));
						}
					}
				} catch (final UnknownMessageException e) {
					System.out.println("Unknown message ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
			System.out.println(e);
			// throw new RuntimeException(e);

			// if (userName != null) {
			// client closed the program or daemon thread timed out client
			// session
			// final ClientSession client = server
			// .getActiveClientData(userName);
			// if (ClientState.TIMED_OUT.equals(client.getCurrentState())) {
			// System.out.println("Client " + userName
			// + " disconnected due to time out");
			// server.terminateSession(userName);
			// } else {
			// System.out
			// .println("Client " + userName + " closed program");
			// client.addState(ClientState.CLIENT_CLOSED);
			// server.terminateSession(userName);
			// }
			// }
		} finally {

		}
	}
}
