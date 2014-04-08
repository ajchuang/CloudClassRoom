package message;

public class ChangePresentTokenResMsg implements Message {
	private static final String head = "CHANGE_PRESENT_TOKEN_RES";
	private final long approverCookieId;
	private final long classId;
	private final String userNameToAdd;
	private final boolean approved;

	public ChangePresentTokenResMsg(final long approverCookieId,
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
		return head + SEPARATOR + approverCookieId + SEPARATOR + classId
				+ SEPARATOR + userNameToAdd + SEPARATOR + approved;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 5 || !head.equals(fields[0])) {
			return null;
		}
		return new ChangePresentTokenResMsg(Long.valueOf(fields[1]),
				Long.valueOf(fields[2]), fields[3],
				Boolean.parseBoolean(fields[4]));
	}
}
