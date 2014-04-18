package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import message.ChangePresentTokenReqMsg;
import message.ChangePresentTokenResMsg;
import message.ClassAdminStatus;
import message.CreateClassReqMsg;
import message.CreateClassResultMsg;
import message.DeleteClassReqMsg;
import message.DeleteClassResMsg;
import message.GetPresentTokenReqMsg;
import message.GetPresentTokenResMsg;
import message.JoinClassApprovalReqMsg;
import message.JoinClassApprovalResMsg;
import message.JoinClassReqMsg;
import message.JoinClassResMsg;
import message.KickUserReqMsg;
import message.KickUserResMsg;
import message.KickUserIndMsg;
import message.ListClassReqMsg;
import message.ListClassResMsg;
import message.LoginReqMsg;
import message.LoginResultMsg;
import message.LogoutReqMsg;
import message.LogoutResultMsg;
import message.Message;
import message.PushContentGetReqMsg;
import message.PushContentGetResMsg;
import message.PushContentNotifyMsg;
import message.PushContentReqMsg;
import message.PushContentResMsg;
import message.QueryClassInfoReqMsg;
import message.QueryClassInfoResMsg;
import message.QueryResultStatus;
import message.QuitClassReqMsg;
import message.QuitClassResMsg;
import message.RetrivePresentTokenIndMsg;
import message.RetrivePresentTokenReqMsg;
import message.RetrivePresentTokenResMsg;

class ServerModel {

	// this will be initialized when server starts
	private final Map<String, ClientSession> activeClients;
	private final Map<Long, String> cookieToUser;
	private final Map<Long, Class> classes;
	private long nextCookieId;
	private long nextClassId;
	private ServerDAO dao;

	ServerModel() {
		dao = DAOFactory.getServerDAO();
		activeClients = new HashMap<String, ClientSession>();
		cookieToUser = new HashMap<Long, String>();
		nextCookieId = 1;
		nextClassId = 1;
		// load info (use users to create client sessions, load classes)
		final List<User> users = dao.loadUsers();
		System.out.println("Users: ");
		for (final User user : users) {
			System.out.println(user);
			activeClients.put(user.getUserName(), new ClientSession(user));
		}
		classes = dao.loadClasses();
		System.out.println("Classes");
		for (final Class c : classes.values()) {
			System.out.println(c);
			if (c.getClassId() <= nextClassId) {
				nextClassId = c.getClassId() + 1;
			}
		}
		System.out.println("Server completely started");
	}

	/**
	 * Get cookieId for user, or assign it for user if user doesn't have one
	 * (first time login)
	 * 
	 * @param userName
	 * @return
	 */
	synchronized long getCookieId(final ClientSession client,
			final String userName) {
		for (final Entry<Long, String> entry : cookieToUser.entrySet()) {
			if (entry.getValue().equals(userName)) {
				return entry.getKey();
			}
		}
		final long newCookieId = nextCookieId++;
		client.setCookieId(newCookieId);
		cookieToUser.put(newCookieId, userName);
		return newCookieId;
	}

	synchronized LogoutResultMsg logout(final LogoutReqMsg logoutReq) {
		final String userName = cookieToUser.get(logoutReq.getCookieId());
		if (userName == null) {
			return new LogoutResultMsg(ClientState.INVALID_COOKIE.toString());
		}
		final ClientSession client = getActiveClientData(userName);
		final ClientState newState = client.logout();
		if (ClientState.LOGGED_OUT.equals(newState)) {
			// close cookie, create new client session
			cookieToUser.remove(logoutReq.getCookieId());
			activeClients.put(userName, new ClientSession(client.getUser()));
		}
		return new LogoutResultMsg(newState.toString());
	}

	synchronized List<Message> login(final LoginReqMsg loginReq,
			final Socket socket) {
		final ClientSession client = getActiveClientData(loginReq.getUserName());
		final List<Message> result = new ArrayList<Message>();
		if (client == null) {
			result.add(new LoginResultMsg(ClientState.INVALID_USER.toString(),
					-1));
		} else {
			final ClientState newState = client.login(loginReq.getPassword(),
					socket);
			final long cookieId;
			if (ClientState.LOGIN_FAIL.equals(newState)) {
				cookieId = -1;
			} else {
				cookieId = getCookieId(client, loginReq.getUserName());
			}
			result.add(new LoginResultMsg(newState.toString(), cookieId));
			if (ClientState.LOGGED_IN.equals(newState)) {
				// add offline message after login
				result.addAll(client.getOfflineMessages());
			}
		}
		return result;
	}

	private ClientSession getLoggedInUser(final long cookieId) {
		final String userName = cookieToUser.get(cookieId);
		if (userName == null) {
			return null;
		}
		final ClientSession client = getActiveClientData(userName);
		if (ClientState.LOGGED_IN.equals(client.getCurrentState())) {
			return client;
		}
		return null;
	}

	synchronized CreateClassResultMsg createClass(
			final CreateClassReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return new CreateClassResultMsg(ClassAdminStatus.NOT_LOGIN, -1);
		}
		if (!(validClient.getUser() instanceof Instructor)) {
			return new CreateClassResultMsg(ClassAdminStatus.NO_PERMISSION, -1);
		}
		for (final Class c : classes.values()) {
			if (c.getClassName().equals(request.getClassName())) {
				return new CreateClassResultMsg(
						ClassAdminStatus.DUPLICATE_NAME, -1);
			}
		}
		final Class newClass = new Class(nextClassId++, request.getClassName(),
				(Instructor) validClient.getUser());
		classes.put(newClass.getClassId(), newClass);
		System.out.println("Inserting new class " + newClass);
		dao.insertNewClass(newClass);
		System.out.println("Class inserted " + newClass.getClassId());
		return new CreateClassResultMsg(ClassAdminStatus.SUCCESS,
				newClass.getClassId());
	}

	synchronized DeleteClassResMsg deleteClass(final DeleteClassReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return new DeleteClassResMsg(ClassAdminStatus.NOT_LOGIN);
		}
		final Class classToDelete = classes.get(request.getClassId());
		if (classToDelete == null) {
			return new DeleteClassResMsg(ClassAdminStatus.INVALID_CLASS_ID);
		}
		if (!validClient.getUser().getUserName()
				.equals(classToDelete.getInstructor().getUserName())) {
			return new DeleteClassResMsg(ClassAdminStatus.NO_PERMISSION);
		}
		classes.remove(classToDelete.getClassId());
		dao.deleteClass(classToDelete.getClassId());
		return new DeleteClassResMsg(ClassAdminStatus.SUCCESS);
	}

	synchronized ListClassResMsg listClass(final ListClassReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return new ListClassResMsg(QueryResultStatus.NOT_LOGIN,
					Collections.<Class> emptyList());
		}
		return new ListClassResMsg(QueryResultStatus.SUCCESS, classes.values());
	}

	static final class MessageToClient {
		// if not null, send message from socket
		final Socket socket;
		// if not null, send by notification
		final String user;
		final Message messagesToSend;

		MessageToClient(final Socket socket, final Message messagesToSend) {
			super();
			this.socket = socket;
			user = null;
			this.messagesToSend = messagesToSend;
		}

		MessageToClient(final String user, final Message messagesToSend) {
			super();
			socket = null;
			this.user = user;
			this.messagesToSend = messagesToSend;
		}
	}

	// return list in case we want to send message to multiple clients
	synchronized List<MessageToClient> getPresenterRequest(
			final Socket requestingSocket, final GetPresentTokenReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new GetPresentTokenResMsg(request
							.getClassId(), "", ClassAdminStatus.NOT_LOGIN
							.toString())));
		}
		final Class requestingClass = classes.get(request.getClassId());
		if (requestingClass == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new GetPresentTokenResMsg(request
							.getClassId(), "",
							ClassAdminStatus.INVALID_CLASS_ID.toString())));
		}
		if (validClient.getUser().getUserName()
				.equals(requestingClass.getInstructor().getUserName())) {
			// instructor doesn't need request token
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new GetPresentTokenResMsg(request
							.getClassId(), requestingClass.getClassName(),
							ClassAdminStatus.NO_PERMISSION.toString())));
		}
		if (!requestingClass.inClass(validClient.getUser().getUserName())) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new GetPresentTokenResMsg(request
							.getClassId(), requestingClass.getClassName(),
							ClassAdminStatus.NOT_IN_CLASS.toString())));
		}
		if (validClient.getUser().getUserName()
				.equals(requestingClass.getPresenter().getUserName())) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new GetPresentTokenResMsg(request
							.getClassId(), requestingClass.getClassName(),
							ClassAdminStatus.ALREADY_PRESENTER.toString())));
		}
		// passed all checks, send approval request to instructor (or add
		// offline message)
		final ClientSession instructorSession = getActiveClientData(requestingClass
				.getInstructor().getUserName());
		final ChangePresentTokenReqMsg approvalReq = new ChangePresentTokenReqMsg(
				validClient.getUser().getUserName(),
				requestingClass.getClassId(), requestingClass.getClassName());
		if (ClientState.LOGGED_IN.equals(instructorSession.getCurrentState())) {
			// prepare a message to send
			return Collections.singletonList(new MessageToClient(
					instructorSession.getSocket(), approvalReq));
		} else {
			// add offline message
			instructorSession.addOfflineMessage(approvalReq);
			return Collections.singletonList(new MessageToClient(
					instructorSession.getUser().getUserName(), approvalReq));
		}
	}

	synchronized List<MessageToClient> changePresentResult(
			final Socket requestingSocket,
			final ChangePresentTokenResMsg request) {
		final ClientSession validClient = getLoggedInUser(request
				.getApproverCookieId());
		if (validClient == null) {
			// Note that we don't send error message
			return Collections.<MessageToClient> emptyList();
		}
		final Class classToUpdate = classes.get(request.getClassId());
		if (classToUpdate == null) {
			// invalid class id
			return Collections.<MessageToClient> emptyList();
		}
		if (!validClient.getUser().getUserName()
				.equals(classToUpdate.getInstructor().getUserName())) {
			// no permission
			return Collections.<MessageToClient> emptyList();
		}
		if (!classToUpdate.inClass(request.getUserNameToAdd())) {
			// not in class
			return Collections.<MessageToClient> emptyList();
		}
		if (classToUpdate.getPresenter().getUserName()
				.equals(request.getUserNameToAdd())) {
			// already presenter
			return Collections.<MessageToClient> emptyList();
		}
		final ClientSession studentSession = getActiveClientData(request
				.getUserNameToAdd());
		if (!(studentSession.getUser() instanceof Student)) {
			// not a student
			return Collections.<MessageToClient> emptyList();
		}
		// passed all checks, send result message to student (or add
		// offline message)
		final String decision;
		if (request.isApproved()) {
			decision = ClassAdminStatus.SUCCESS.toString();
			classToUpdate.assignPresenter(studentSession.getUser());
			dao.assignPresenter(classToUpdate.getClassId(), studentSession
					.getUser().getUserName());
		} else {
			decision = ClassAdminStatus.DENIED.toString();
		}
		final GetPresentTokenResMsg result = new GetPresentTokenResMsg(
				classToUpdate.getClassId(), classToUpdate.getClassName(),
				decision);
		if (ClientState.LOGGED_IN.equals(studentSession.getCurrentState())) {
			// prepare a message to send
			return Collections.singletonList(new MessageToClient(studentSession
					.getSocket(), result));
		} else {
			// add offline message
			studentSession.addOfflineMessage(result);
			return Collections.singletonList(new MessageToClient(studentSession
					.getUser().getUserName(), result));
		}
	}

	synchronized QueryClassInfoResMsg queryClassInfo(
			final QueryClassInfoReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return new QueryClassInfoResMsg(
					ClassAdminStatus.NOT_LOGIN.toString(), "",
					Collections.<String> emptyList());
		}
		final Class classToQuery = classes.get(request.getClassId());
		if (classToQuery == null) {
			return new QueryClassInfoResMsg(
					ClassAdminStatus.INVALID_CLASS_ID.toString(), "",
					Collections.<String> emptyList());
		}
		final List<String> students = new ArrayList<String>();
		for (final User student : classToQuery.getStudents()) {
			students.add(student.getUserName());
		}
		return new QueryClassInfoResMsg(ClassAdminStatus.SUCCESS.toString(),
				classToQuery.getInstructor().getUserName(), students);
	}

	synchronized QuitClassResMsg quitClass(final QuitClassReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return new QuitClassResMsg(ClassAdminStatus.NOT_LOGIN.toString());
		}
		final Class classToQuit = classes.get(request.getClassId());
		if (classToQuit == null) {
			return new QuitClassResMsg(
					ClassAdminStatus.INVALID_CLASS_ID.toString());
		}
		if (!classToQuit.inClass(validClient.getUser().getUserName())) {
			return new QuitClassResMsg(ClassAdminStatus.NOT_IN_CLASS.toString());
		}
		classToQuit.leaveClass((Student) validClient.getUser());
		dao.leaveClass(classToQuit.getClassId(), validClient.getUser()
				.getUserName());
		return new QuitClassResMsg(ClassAdminStatus.SUCCESS.toString());
	}

	synchronized List<MessageToClient> kickUserFromClass(
			final Socket requestingSocket, final KickUserReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new KickUserResMsg(
							ClassAdminStatus.NOT_LOGIN.toString())));
		}
		final Class classToKick = classes.get(request.getClassId());
		if (classToKick == null) {
			// invalid class id
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new KickUserResMsg(
							ClassAdminStatus.INVALID_CLASS_ID.toString())));
		}
		if (!(validClient.getUser().getUserName().equals(classToKick
				.getInstructor().getUserName()))) {
			// no permission
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new KickUserResMsg(
							ClassAdminStatus.NO_PERMISSION.toString())));
		}
		if (!classToKick.inClass(request.getStudentToKick())) {
			// not in class
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new KickUserResMsg(
							ClassAdminStatus.NOT_IN_CLASS.toString())));
		}
		final ClientSession studentSession = getActiveClientData(request
				.getStudentToKick());
		classToKick.leaveClass((Student) studentSession.getUser());
		dao.leaveClass(classToKick.getClassId(), request.getStudentToKick());

		final KickUserResMsg msgToInstructor = new KickUserResMsg(
				ClassAdminStatus.SUCCESS.toString());
		final KickUserIndMsg msgToStudent = new KickUserIndMsg(
				ClassAdminStatus.SUCCESS.toString(), classToKick.getClassId(),
				classToKick.getClassName());
		final List<MessageToClient> result = new ArrayList<MessageToClient>();
		result.add(new MessageToClient(requestingSocket, msgToInstructor));
		if (ClientState.LOGGED_IN.equals(studentSession.getCurrentState())) {
			result.add(new MessageToClient(studentSession.getSocket(),
					msgToStudent));
		} else {
			// add offline message
			studentSession.addOfflineMessage(msgToStudent);
			result.add(new MessageToClient(studentSession.getUser()
					.getUserName(), msgToStudent));
		}
		return result;
	}

	synchronized List<MessageToClient> pushContent(
			final Socket requestingSocket, final PushContentReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new PushContentResMsg(
							ClassAdminStatus.NOT_LOGIN.toString())));
		}
		final Class classToAdd = classes.get(request.getClassId());
		if (classToAdd == null) {
			// invalid class id
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new PushContentResMsg(
							ClassAdminStatus.INVALID_CLASS_ID.toString())));
		}
		if (!(validClient.getUser().getUserName().equals(classToAdd
				.getPresenter().getUserName()))) {
			// no permission
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new PushContentResMsg(
							ClassAdminStatus.NO_PERMISSION.toString())));
		}
		if (classToAdd.hasContent(request.getContentId())) {
			// already has content
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new PushContentResMsg(
							ClassAdminStatus.ALREADY_IN_CLASS.toString())));
		}
		classToAdd.pushContent(request.getContentId());
		// TODO not push to database for now
		final PushContentResMsg toInstructor = new PushContentResMsg(
				ClassAdminStatus.SUCCESS.toString());
		final PushContentNotifyMsg toStudent = new PushContentNotifyMsg(
				request.getClassId(), request.getContentId());
		final List<MessageToClient> result = new ArrayList<MessageToClient>();
		result.add(new MessageToClient(requestingSocket, toInstructor));
		for (final ClientSession client : activeClients.values()) {
			if (client.getUser() instanceof Student) {
				if (ClientState.LOGGED_IN.equals(client.getCurrentState())) {
					result.add(new MessageToClient(client.getSocket(),
							toStudent));
				} else {
					// off line
					client.addOfflineMessage(toStudent);
					result.add(new MessageToClient(client.getUser()
							.getUserName(), toStudent));
				}
			}
		}
		return result;
	}

	// synchronized PushContentGetResMsg getContent(
	// final PushContentGetReqMsg request) {
	// final ClientSession validClient = getLoggedInUser(request.getCookieId());
	// if (validClient == null) {
	// return new PushContentGetResMsg(
	// ClassAdminStatus.NOT_LOGIN.toString(),
	// request.getContentId(), "", new byte[0]);
	// }
	// final Class classToQuery = classes.get(request.getClassId());
	// if (classToQuery == null) {
	// return new PushContentGetResMsg(
	// ClassAdminStatus.INVALID_CLASS_ID.toString(),
	// request.getContentId(), "", new byte[0]);
	// }
	// if (!classToQuery.getInstructor().getUserName()
	// .equals(validClient.getUser().getUserName())
	// && !classToQuery.inClass(validClient.getUser().getUserName())) {
	// return new PushContentGetResMsg(
	// ClassAdminStatus.NOT_IN_CLASS.toString(),
	// request.getContentId(), "", new byte[0]);
	// }
	// if (!classToQuery.hasContent(request.getContentId())) {
	// return new PushContentGetResMsg(
	// ClassAdminStatus.CONTENT_NOT_IN_CLASS.toString(),
	// request.getContentId(), "", new byte[0]);
	// }
	// final ClassContent content = classToQuery.getContent(request
	// .getContentId());
	// return new PushContentGetResMsg(ClassAdminStatus.SUCCESS.toString(),
	// request.getContentId(), content.getContentType(),
	// content.getContents());
	// }

	// return list in case we want to send message to multiple clients
	synchronized List<MessageToClient> retrivePresentToken(
			final Socket requestingSocket,
			final RetrivePresentTokenReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new RetrivePresentTokenResMsg(
							ClassAdminStatus.NOT_LOGIN.toString())));
		}
		final Class requestingClass = classes.get(request.getClassId());
		if (requestingClass == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new RetrivePresentTokenResMsg(
							ClassAdminStatus.INVALID_CLASS_ID.toString())));
		}
		if (!validClient.getUser().getUserName()
				.equals(requestingClass.getInstructor().getUserName())) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new RetrivePresentTokenResMsg(
							ClassAdminStatus.NO_PERMISSION.toString())));
		}
		if (validClient.getUser().getUserName()
				.equals(requestingClass.getPresenter().getUserName())) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new RetrivePresentTokenResMsg(
							ClassAdminStatus.ALREADY_PRESENTER.toString())));
		}
		// passed all checks, send approval request to current presenter (or add
		// offline message), and instructor
		final ClientSession currentPresent = getActiveClientData(requestingClass
				.getPresenter().getUserName());
		requestingClass.assignPresenter(requestingClass.getInstructor());
		dao.assignPresenter(requestingClass.getClassId(), requestingClass
				.getInstructor().getUserName());
		final RetrivePresentTokenResMsg toInstructor = new RetrivePresentTokenResMsg(
				ClassAdminStatus.SUCCESS.toString());
		final RetrivePresentTokenIndMsg toOldPresenter = new RetrivePresentTokenIndMsg(
				requestingClass.getClassId(), requestingClass.getClassName());
		final List<MessageToClient> result = new ArrayList<MessageToClient>();
		result.add(new MessageToClient(requestingSocket, toInstructor));
		if (ClientState.LOGGED_IN.equals(currentPresent.getCurrentState())) {
			result.add(new MessageToClient(currentPresent.getSocket(),
					toOldPresenter));
		} else {
			result.add(new MessageToClient(currentPresent.getUser()
					.getUserName(), toOldPresenter));
			// add offline message
			currentPresent.addOfflineMessage(toOldPresenter);
		}
		return result;
	}

	synchronized List<MessageToClient> joinClassResult(
			final Socket requestingSocket, final JoinClassApprovalResMsg request) {
		final ClientSession validClient = getLoggedInUser(request
				.getApproverCookieId());
		if (validClient == null) {
			// Note that we don't send error message
			return Collections.<MessageToClient> emptyList();
		}
		final Class classToJoin = classes.get(request.getClassId());
		if (classToJoin == null) {
			// invalid class id
			return Collections.<MessageToClient> emptyList();
		}
		if (!validClient.getUser().getUserName()
				.equals(classToJoin.getInstructor().getUserName())) {
			// no permission
			return Collections.<MessageToClient> emptyList();
		}
		if (classToJoin.inClass(request.getUserNameToAdd())) {
			// already in class
			return Collections.<MessageToClient> emptyList();
		}
		final ClientSession studentSession = getActiveClientData(request
				.getUserNameToAdd());
		if (!(studentSession.getUser() instanceof Student)) {
			// not a student
			return Collections.<MessageToClient> emptyList();
		}
		// passed all checks, send result message to student (or add
		// offline message)
		final String decision;
		if (request.isApproved()) {
			decision = ClassAdminStatus.SUCCESS.toString();
			classToJoin.joinClass((Student) studentSession.getUser());
			dao.addStudentToClass(classToJoin.getClassId(), studentSession
					.getUser().getUserName());
		} else {
			decision = ClassAdminStatus.DENIED.toString();
		}
		final JoinClassResMsg result = new JoinClassResMsg(
				classToJoin.getClassId(), classToJoin.getClassName(), decision);
		if (ClientState.LOGGED_IN.equals(studentSession.getCurrentState())) {
			// prepare a message to send
			return Collections.singletonList(new MessageToClient(studentSession
					.getSocket(), result));
		} else {
			// add offline message
			studentSession.addOfflineMessage(result);
			return Collections.singletonList(new MessageToClient(studentSession
					.getUser().getUserName(), result));
		}
	}

	// return list in case we want to send message to multiple clients
	synchronized List<MessageToClient> joinClassRequest(
			final Socket requestingSocket, final JoinClassReqMsg request) {
		final ClientSession validClient = getLoggedInUser(request.getCookieId());
		if (validClient == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new JoinClassResMsg(request.getClassId(),
							"", ClassAdminStatus.NOT_LOGIN.toString())));
		}
		final Class classToJoin = classes.get(request.getClassId());
		if (classToJoin == null) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new JoinClassResMsg(request.getClassId(),
							"", ClassAdminStatus.INVALID_CLASS_ID.toString())));
		}
		if (!(validClient.getUser() instanceof Student)) {
			// we only allow student to join a class
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new JoinClassResMsg(request.getClassId(),
							classToJoin.getClassName(),
							ClassAdminStatus.NO_PERMISSION.toString())));
		}
		if (classToJoin.inClass(validClient.getUser().getUserName())) {
			return Collections.singletonList(new MessageToClient(
					requestingSocket, new JoinClassResMsg(request.getClassId(),
							classToJoin.getClassName(),
							ClassAdminStatus.ALREADY_IN_CLASS.toString())));
		}
		// passed all checks, send approval request to instructor (or add
		// offline message)
		final ClientSession instructorSession = getActiveClientData(classToJoin
				.getInstructor().getUserName());
		final JoinClassApprovalReqMsg approvalReq = new JoinClassApprovalReqMsg(
				validClient.getUser().getUserName(), classToJoin.getClassId(),
				classToJoin.getClassName());
		if (ClientState.LOGGED_IN.equals(instructorSession.getCurrentState())) {
			// prepare a message to send
			return Collections.singletonList(new MessageToClient(
					instructorSession.getSocket(), approvalReq));
		} else {
			// add offline message
			instructorSession.addOfflineMessage(approvalReq);
			return Collections.singletonList(new MessageToClient(
					instructorSession.getUser().getUserName(), approvalReq));
		}
	}

	// this never returns null
	private ClientSession getActiveClientData(final String user) {
		return activeClients.get(user);
	}
}
