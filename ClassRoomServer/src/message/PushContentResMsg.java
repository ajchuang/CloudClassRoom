package message;

public class PushContentResMsg extends AbstractMessage {

	private static final String head = "PUSH_CONTENT_RES";
	private final String status;

	public PushContentResMsg(String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR + END;
	}

}
