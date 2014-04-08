package message;

import java.util.Collection;

public class ListClassResMsg implements Message {
	private static final String head = "LIST_CLASS_RES";

	private final QueryResultStatus status;
	private final Collection<String> classInfos;

	public ListClassResMsg(final QueryResultStatus status,
			final Collection<String> classInfos) {
		super();
		this.status = status;
		this.classInfos = classInfos;
	}

	@Override
	public String toMseeage() {
		final StringBuilder str = new StringBuilder(head + SEPARATOR
				+ status.toString() + SEPARATOR + classInfos.size());
		for (final String classInfo : classInfos) {
			str.append(SEPARATOR + classInfo);
		}
		return str.toString();
	}
}
