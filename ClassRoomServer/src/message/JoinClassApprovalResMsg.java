package message;

public class JoinClassApprovalResMsg extends AbstractMessage {

	private static final String head = "JOIN_CLASS_APPROVAL_RES";
	private final long approverCookieId;
	private final long classId;
	private final String userNameToAdd;
	private final boolean approved;

	public JoinClassApprovalResMsg(final long approverCookieId,
			final long classId, final String userNameToAdd,
			final boolean approved) {
		super();
		this.approverCookieId = approverCookieId;
		this.classId = classId;
		this.userNameToAdd = userNameToAdd;
		this.approved = approved;
	}

	public long getApproverCookieId() {
		return approverCookieId;
	}

	public long getClassId() {
		return classId;
	}

	public String getUserNameToAdd() {
		return userNameToAdd;
	}

	public boolean isApproved() {
		return approved;
	}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + approverCookieId + SEPARATOR + classId
		// + SEPARATOR + userNameToAdd + SEPARATOR + approved;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 5 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		return new JoinClassApprovalResMsg(Long.valueOf(getData(fields[1])),
				Long.valueOf(getData(fields[2])), getData(fields[3]),
				Boolean.parseBoolean(getData(fields[4])));
	}
}
