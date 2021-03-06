package message;

public class PushContentReqMsg extends AbstractMessage {

	private static final String head = "PUSH_CONTENT_REQ";
	private final long cookieId;
	private final long classId;
	private final String contentId;
	private final String contentType;
	//private final int nBytes;
	//private final byte[] bytes;

	public PushContentReqMsg(final long cookieId, final long classId,
			final String contentId, final String contentType/*, final int nBytes,
			final byte[] bytes*/) {
		super();
		this.cookieId = cookieId;
		this.classId = classId;
		this.contentId = contentId.trim ();
		this.contentType = contentType;
		//this.nBytes = nBytes;
		//this.bytes = bytes;
	}

	public long getCookieId() {
		return cookieId;
	}

	public long getClassId() {
		return classId;
	}

	public String getContentId() {
		return contentId;
	}

	public String getContentType() {
		return contentType;
	}

	//public int getnBytes() {
	//	return nBytes;
	//}

	//public byte[] getBytes() {
	//	return bytes;
	//}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException();
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length != 5 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		//final int nBytes = Integer.parseInt(getData(fields[5]));
	//	final byte[] bytes = new byte[nBytes];
		//for (int i = 0; i < nBytes; i++) {
		//	bytes[i] = Byte.parseByte(getData(fields[6 + i]));
		//}

		return new PushContentReqMsg(Long.parseLong(getData(fields[1])),
				Long.parseLong(getData(fields[2])),
				getData(fields[3]), getData(fields[4])/*, nBytes,
				bytes*/);
	}
}
