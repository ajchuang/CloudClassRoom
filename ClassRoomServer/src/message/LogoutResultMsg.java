package message;

public class LogoutResultMsg extends AbstractMessage {

	private static final String head = "LOGOUT_RES";

	private final String status;

	public LogoutResultMsg(final String status) {
		super();
		this.status = status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR + END;
	}
}
