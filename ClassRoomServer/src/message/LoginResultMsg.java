package message;

public class LoginResultMsg extends AbstractMessage {

	private static final String head = "LOGIN_RES";
	private final String status;
	private final long cookieId;
	private final String role;

	public LoginResultMsg(final String status, final long cookieId, final String role
		) {
		super();
		this.status = status;
		this.cookieId = cookieId;
		this.role = role;
	}

	public String getStatus() {
		return status;
	}

	public long getCookieId() {
		return cookieId;
	}
	
	public String getRole(){
		return role;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + wrapDataField(status) + SEPARATOR
				+ wrapDataField(cookieId) + SEPARATOR +wrapDataField(role) + SEPARATOR + END;
	}
}
