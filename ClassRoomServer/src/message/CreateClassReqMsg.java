package message;

//to server
public class CreateClassReqMsg extends AbstractMessage {
	private static final String head = "CREATE_CLASS_REQ";
	private final String className;
	private final long cookieId;

	public CreateClassReqMsg(final String className, final long cookieId) {
		super();
		this.className = className;
		this.cookieId = cookieId;
	}

	public String getClassName() {
		return className;
	}

	public long getCookieId() {
		return cookieId;
	}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + className + SEPARATOR + cookieId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new CreateClassReqMsg(getData(fields[1]),
				Long.valueOf(getData(fields[2])));
	}
}
