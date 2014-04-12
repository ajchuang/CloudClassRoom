package message;

public class LoginReqMsg extends AbstractMessage {
	private static final String head = "LOGIN_REQ";
	private final String userName;
	private final String password;

	public LoginReqMsg(final String userName, final String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + userName + SEPARATOR + password;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new LoginReqMsg(getData(fields[1]), getData(fields[2]));
	}
}
