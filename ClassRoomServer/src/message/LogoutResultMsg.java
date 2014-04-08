package message;

public class LogoutResultMsg implements Message {

	private static final String head = "LOGOUT_RES";

	private final String status;

	public LogoutResultMsg(final String status) {
		super();
		this.status = status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + status;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 2 || !head.equals(fields[0])) {
			return null;
		}
		return new LogoutResultMsg(fields[1]);
	}

}
