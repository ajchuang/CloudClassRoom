package message;

public class KickUserResMsg implements Message {
	
	private static final String head = "KICK_USER_RES";
	private final String status;

	public KickUserResMsg(final String status) {
		super();
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + status;
	}
}
