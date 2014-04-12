package message;

public class PushContentGetReqMsg extends AbstractMessage {

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
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + cookieId + SEPARATOR + classId + SEPARATOR
		// + contentId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 4 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new PushContentGetReqMsg(Long.valueOf(getData(fields[1])),
				Long.valueOf(getData(fields[2])),
				Long.valueOf(getData(fields[3])));
	}
}
