package message;

public class RetrivePresentTokenIndMsg extends AbstractMessage {

	private static final String head = "RETRIEVE_PRESENT_TOKEN_IND";
	private final long classId;
	private final String className;

	public RetrivePresentTokenIndMsg(final long classId, final String className) {
		super();
		this.classId = classId;
		this.className = className;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(classId) + SEPARATOR
				+ wrapDataField(className) + SEPARATOR + END;
	}
}
