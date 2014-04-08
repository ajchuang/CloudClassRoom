package message;

public class ListClassReqMsg implements Message {
	private static final String head = "LIST_CLASS_REQ";
	private final long cookieId;

	public ListClassReqMsg(final long cookieId) {
		super();
		this.cookieId = cookieId;
	}

	public long getCookieId() {
		return cookieId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + cookieId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 2 || !head.equals(fields[0])) {
			return null;
		}
		return new ListClassReqMsg(Long.valueOf(fields[1]));
	}
}
