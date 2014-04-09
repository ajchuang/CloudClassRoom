package server;

public enum ClientState {
	NOT_CONNECTED,
	INVALID_USER,
	LOGIN_FAIL,
	//BLOCKED,
	LOGGED_IN,
	DUPLICATE,
	LOGGED_OUT,
	TIMED_OUT,
	CLIENT_CLOSED,
	INVALID_COOKIE,
	LOGOUT_FAIL;
}