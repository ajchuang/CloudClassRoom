package message;

public class KickUserIndMsg extends AbstractMessage {

	private static final String head = "KICK_USER_IND";
	private final String status;
	private final long classId;
	private final String className;

	public KickUserIndMsg(final String status, final long classId,
			final String className) {
		super();
		this.status = status;
		this.classId = classId;
		this.className = className;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR
				+ wrapDataField(classId) + SEPARATOR + wrapDataField(className)
				+ SEPARATOR + END;
	}
}
