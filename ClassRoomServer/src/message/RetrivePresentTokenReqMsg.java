package message;

public class RetrivePresentTokenReqMsg implements Message {

	private static final String head = "RETRIVE_PRESENT_TOKEN_REQ";
	private final long cookieId;
	private final long classId;

	public RetrivePresentTokenReqMsg(final long cookieId, final long classId) {
		super();
		this.cookieId = cookieId;
		this.classId = classId;
	}

	public long getCookieId() {
		return cookieId;
	}

	public long getClassId() {
		return classId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + cookieId + SEPARATOR + classId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		return new RetrivePresentTokenReqMsg(Long.valueOf(fields[1]),
				Long.valueOf(fields[2]));
	}
}
