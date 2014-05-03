package message;

public class CreateUsrResMsg extends AbstractMessage {

	private static final String head = "NEW_USER_RES";
	private final String status;
	
	public CreateUsrResMsg(final String status) {
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
