package server;

import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import message.LoginReqMsg;
import message.Message;

public class ClientSession {
	private static final class TimedState {
		private final ClientState state;
		private Date lastUpdated;

		private TimedState(final ClientState state) {
			this.state = state;
			lastUpdated = new Date();
		}
	}

	private final User user;
	private Long cookieId;
	private final List<TimedState> states;
	private final List<Message> offlineMessages;
	private Socket socket;
	private DeviceType deviceType;
	private String tokenId;

	public ClientSession(final User user) {
		this.user = user;
		cookieId = null;
		states = new LinkedList<TimedState>();
		offlineMessages = new LinkedList<Message>();
		addState(ClientState.NOT_CONNECTED);
		deviceType = null;
		tokenId = null;
	}

	public Socket getSocket() {
		return socket;
	}

	private void setSocket(final Socket socket) {
		this.socket = socket;
	}

	private void addState(final ClientState state) {
		states.add(new TimedState(state));
	}

	private TimedState getCurrentTimedState() {
		if (states.isEmpty()) {
			return null;
		}
		return states.get(states.size() - 1);
	}

	ClientState getCurrentState() {
		final TimedState state = getCurrentTimedState();
		return state.state;
	}

	public ClientState login(final LoginReqMsg loginReq, final Socket socket) {
		final String password = loginReq.getPassword();

		final ClientState state = getCurrentState();
		if (ClientState.NOT_CONNECTED.equals(state)
				|| ClientState.LOGIN_FAIL.equals(state)
				|| ClientState.SUSPENDED.equals(state)) {
			setSocket(socket);
			if (user.getPassword().equals(password)) {
				deviceType = loginReq.getDeviceType();
				tokenId = loginReq.getTokenId();
				addState(ClientState.LOGGED_IN);
				return ClientState.LOGGED_IN;
			} else {
				addState(ClientState.LOGIN_FAIL);
				return ClientState.LOGIN_FAIL;
			}
		} else if (ClientState.LOGGED_IN.equals(state)) {
			return ClientState.DUPLICATE;
		} else {
			// can't happen
			throw new RuntimeException("Can't try login from state " + state);
		}
	}

	public ClientState logout() {
		final ClientState state = getCurrentState();
		if (ClientState.LOGGED_IN.equals(state)) {
			addState(ClientState.LOGGED_OUT);
			return ClientState.LOGGED_OUT;
		} else {
			return ClientState.LOGOUT_FAIL;
		}
	}

	public ClientState sessionBack(final long cookieId) {
		final TimedState lastState = getCurrentTimedState();
		final Date loginAfter = new Date(new Date().getTime()
				- ServerConfigs.TIME_OUT * 1000);
		if (this.cookieId != cookieId
				|| !ClientState.LOGGED_IN.equals(lastState.state)) {
			return ClientState.LOGIN_FAIL;
		} else if (lastState.lastUpdated.before(loginAfter)) {
			addState(ClientState.TIMED_OUT);
			return ClientState.TIMED_OUT;
		} else {
			updateLastStateTime();
			return lastState.state;
		}
	}

	public void updateLastStateTime() {
		final TimedState state = getCurrentTimedState();
		if (state != null) {
			state.lastUpdated = new Date();
		}
	}

	public void setCookieId(final Long cookieId) {
		this.cookieId = cookieId;
	}

	public List<Message> getOfflineMessages() {
		return offlineMessages;
	}

	public void clearOfflineMessages() {
		offlineMessages.clear();
	}

	public void addOfflineMessage(final Message message) {
		offlineMessages.add(message);
	}

	public void suspendClientSession() {
		addState(ClientState.SUSPENDED);
	}

	public User getUser() {
		return user;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public String getTokenId() {
		return tokenId;
	}

	public boolean canPushNotification() {
		return deviceType != null && deviceType.isPushNotification();
	}
}
