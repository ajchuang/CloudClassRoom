package message;

public class QuitClassResMsg implements Message {
	
	private static final String head = "QUIT_CLASS_RES";
	private final String status;

	public QuitClassResMsg(final String status) {
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
