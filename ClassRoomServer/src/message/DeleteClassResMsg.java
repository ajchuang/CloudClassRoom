package message;

public class DeleteClassResMsg extends AbstractMessage {

	private static final String head = "DELETE_CLASS_RES";
	private final ClassAdminStatus status;

	public DeleteClassResMsg(final ClassAdminStatus status) {
		super();
		this.status = status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status.toString()) + SEPARATOR
				+ END;
	}
}
