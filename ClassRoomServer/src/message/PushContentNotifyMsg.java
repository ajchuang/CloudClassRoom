package message;

public class PushContentNotifyMsg extends AbstractMessage {

	private static final String head = "PUSH_CONTENT_NOTIFY";
	private final long classId;
	private final String contentId;

	public PushContentNotifyMsg(final long classId, final String contentId) {
		super();
		this.classId = classId;
		this.contentId = contentId;
	}

	public long getClassId() {
		return classId;
	}

	public String getContentId() {
		return contentId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(classId) + SEPARATOR
				+ wrapDataField(contentId) + SEPARATOR + END;
	}
}
