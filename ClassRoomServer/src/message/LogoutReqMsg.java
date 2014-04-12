package message;

public class LogoutReqMsg extends AbstractMessage {

	private static final String head = "LOGOUT_REQ";
	private final long cookieId;

	public LogoutReqMsg(final long cookieId) {
		super();
		this.cookieId = cookieId;
	}

	public long getCookieId() {
		return cookieId;
	}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + cookieId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 2 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new LogoutReqMsg(Long.valueOf(getData(fields[1])));
	}
}
