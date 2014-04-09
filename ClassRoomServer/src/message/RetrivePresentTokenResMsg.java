package message;

public class RetrivePresentTokenResMsg implements Message {

	private static final String head = "RETRIVE_PRESENT_TOKEN_RES";
	private final String status;

	public RetrivePresentTokenResMsg(final String status) {
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