package message;


public class LoginReqMsg implements Message {
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
		return head + SEPARATOR + userName + SEPARATOR + password;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		return new LoginReqMsg(fields[1], fields[2]);
	}
}
