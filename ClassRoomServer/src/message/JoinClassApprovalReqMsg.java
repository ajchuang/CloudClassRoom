package message;

/**
 * Server to client (instructor)
 * 
 */
public class JoinClassApprovalReqMsg implements Message {

	private static final String head = "JOIN_CLASS_APPROVAL_REQ";
	private final String requestUser;
	private final long classId;
	private final String className;

	public JoinClassApprovalReqMsg(final String requestUser,
			final long classId, final String className) {
		super();
		this.requestUser = requestUser;
		this.classId = classId;
		this.className = className;
	}

	public String getRequestUser() {
		return requestUser;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + requestUser + SEPARATOR + classId + SEPARATOR
				+ className;
	}
}
