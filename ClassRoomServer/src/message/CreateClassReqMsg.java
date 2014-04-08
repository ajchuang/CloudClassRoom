package message;

public class CreateClassReqMsg implements Message {
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
		return head + SEPARATOR + className + SEPARATOR + cookieId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		return new CreateClassReqMsg(fields[1], Long.valueOf(fields[2]));
	}
}
