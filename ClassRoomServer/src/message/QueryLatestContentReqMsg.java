package message;

public class QueryLatestContentReqMsg extends AbstractMessage {

	private static final String head = "QUERY_LATEST_CONTENT_REQ";
	private final long cookieId;
	private final long classId;

	public QueryLatestContentReqMsg(final long cookieId, final long classId) {
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
		return new QueryLatestContentReqMsg(Long.valueOf(getData(fields[1])),
				Long.valueOf(getData(fields[2])));
	}
}
