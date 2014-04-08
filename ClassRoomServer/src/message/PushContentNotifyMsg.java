package message;

public class PushContentNotifyMsg implements Message {

	private static final String head = "PUSH_CONTENT_NOTIFY";
	private final long classId;
	private final long contentId;

	public PushContentNotifyMsg(final long classId, final long contentId) {
		super();
		this.classId = classId;
		this.contentId = contentId;
	}

	public long getClassId() {
		return classId;
	}

	public long getContentId() {
		return contentId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + classId + SEPARATOR + contentId;
	}
}
