package message;

public class QueryClassInfoReqMsg implements Message{

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
		return head + SEPARATOR + cookieId + SEPARATOR + classId;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 3 || !head.equals(fields[0])) {
			return null;
		}
		return new QueryClassInfoReqMsg(Long.valueOf(fields[1]),
				Long.valueOf(fields[2]));
	}
}
