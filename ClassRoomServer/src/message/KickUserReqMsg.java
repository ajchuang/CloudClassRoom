package message;

public class KickUserReqMsg implements Message {

	private static final String head = "KICK_USER_REQ";
	private final long cookieId;
	private final long classId;
	private final String studentToKick;

	public KickUserReqMsg(final long cookieId, final long classId,
			final String studentToKick) {
		super();
		this.cookieId = cookieId;
		this.classId = classId;
		this.studentToKick = studentToKick;
	}

	public long getCookieId() {
		return cookieId;
	}

	public long getClassId() {
		return classId;
	}

	public String getStudentToKick() {
		return studentToKick;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + cookieId + SEPARATOR + classId + SEPARATOR
				+ studentToKick;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 4 || !head.equals(fields[0])) {
			return null;
		}
		return new KickUserReqMsg(Long.valueOf(fields[1]),
				Long.valueOf(fields[2]), fields[3]);
	}
}
