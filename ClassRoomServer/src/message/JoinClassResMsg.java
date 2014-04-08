package message;

public class JoinClassResMsg implements Message {

	private static final String head = "JOIN_CLASS_RES";
	private final long classId;
	private final String className;
	private final String status;

	public JoinClassResMsg(final long classId, final String className,
			final String status) {
		super();
		this.classId = classId;
		this.className = className;
		this.status = status;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toMseeage() {
		return head + SEPARATOR + classId + SEPARATOR + className + SEPARATOR
				+ status;
	}

}
