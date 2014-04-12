package message;

public class LoginResultMsg extends AbstractMessage {

	private static final String head = "LOGIN_RES";
	private final String status;
	private final long cookieId;

	public LoginResultMsg(final String status, final long cookieId) {
		super();
		this.status = status;
		this.cookieId = cookieId;
	}

	public String getStatus() {
		return status;
	}

	public long getCookieId() {
		return cookieId;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR
				+ wrapDataField(cookieId) + SEPARATOR + END;
	}
}
