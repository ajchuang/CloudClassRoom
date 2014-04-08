package message;

public class ChangePresentTokenReqMsg implements Message {

	private static final String head = "CHANGE_PRESENT_TOKEN_REQ";
	private final String userName;
	private final long classId;
	private final String className;

	public ChangePresentTokenReqMsg(final String userName, final long classId,
			final String className) {
		super();
		this.userName = userName;
		this.classId = classId;
		this.className = className;
	}

	public String getUserName() {
		return userName;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + userName + SEPARATOR + classId + SEPARATOR
				+ className;
	}
}
