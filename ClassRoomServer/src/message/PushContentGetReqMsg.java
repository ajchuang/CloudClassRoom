package message;

public class PushContentGetReqMsg implements Message {

	private static final String head = "JOIN_CLASS_REQ";
	private final long cookieId;
	private final long classId;
	private final long contentId;

	public PushContentGetReqMsg(long cookieId, long classId, long contentId) {
		super();
		this.cookieId = cookieId;
		this.classId = classId;
		this.contentId = contentId;
	}

	public long getCookieId() {
		return cookieId;
	}

	public long getClassId() {
		return classId;
	}

	public long getContentId() {
		return contentId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + cookieId + SEPARATOR + classId + SEPARATOR
				+ contentId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 4 || !head.equals(fields[0])) {
			return null;
		}
		return new PushContentGetReqMsg(Long.valueOf(fields[1]),
				Long.valueOf(fields[2]), Long.valueOf(fields[3]));
	}
}
