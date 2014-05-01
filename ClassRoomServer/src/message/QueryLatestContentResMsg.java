package message;

public class QueryLatestContentResMsg extends AbstractMessage {

	private static final String head = "QUERY_LATEST_CONTENT_RES";
	private final String status;
	private final long classId;
	private final String contentId;

	public QueryLatestContentResMsg(final String status, final long classId,
			final String contentId) {
		super();
		this.status = status;
		this.classId = classId;
		this.contentId = contentId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR
				+ wrapDataField(classId) + SEPARATOR + wrapDataField(contentId)
				+ SEPARATOR + END;
	}
}
