package message;

public class PushContentGetResMsg implements Message {

	private static final String head = "JOIN_CLASS_RES";
	private final String status;
	private final long contentId;
	private final String contentType;
	private final int nBytes;
	private final byte[] bytes;

	public PushContentGetResMsg(String status, long contentId,
			String contentType, byte[] bytes) {
		super();
		this.status = status;
		this.contentId = contentId;
		this.contentType = contentType;
		this.nBytes = bytes.length;
		this.bytes = bytes;
	}

	public String getStatus() {
		return status;
	}

	public long getContentId() {
		return contentId;
	}

	public String getContentType() {
		return contentType;
	}

	public int getnBytes() {
		return nBytes;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public String toMseeage() {
		final StringBuilder str = new StringBuilder(head + SEPARATOR
				+ status.toString() + SEPARATOR + contentId + SEPARATOR
				+ contentType + SEPARATOR + nBytes);
		for (final byte oneByte : bytes) {
			str.append(SEPARATOR + oneByte);
		}
		return str.toString();
	}
}
