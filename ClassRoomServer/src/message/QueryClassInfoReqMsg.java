package message;

public class QueryClassInfoReqMsg extends AbstractMessage {

	private static final String head = "QUERY_CLASS_INFO_REQ";
	private final long cookieId;
	private final long classId;

	public QueryClassInfoReqMsg(final long cookieId, final long classId) {
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
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + cookieId + SEPARATOR + classId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new QueryClassInfoReqMsg(Long.valueOf(getData(fields[1])),
				Long.valueOf(getData(fields[2])));
	}
}
