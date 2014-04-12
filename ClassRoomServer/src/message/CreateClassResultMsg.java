package message;

public class CreateClassResultMsg extends AbstractMessage {

	private static final String head = "CREATE_CLASS_RES";
	private final ClassAdminStatus status;
	private final long classId;

	public CreateClassResultMsg(final ClassAdminStatus status,
			final long classId) {
		super();
		this.status = status;
		this.classId = classId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status.toString()) + SEPARATOR
				+ wrapDataField(classId) + SEPARATOR + END;
	}
}
