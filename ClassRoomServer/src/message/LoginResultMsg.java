package message;

public class LoginResultMsg implements Message {

	private static final String head = "LOGIN_RES";
	private final String status;
	private final long cookieId;

	public LoginResultMsg(final String status, final long cookieId) {
		super();
		this.status = status;
		this.cookieId = cookieId;
	}

	public String getStatus() {
		return status;
	}

	public long getCookieId() {
		return cookieId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + status + SEPARATOR + cookieId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		return new LoginResultMsg(fields[1], Long.valueOf(fields[2]));
	}
}
