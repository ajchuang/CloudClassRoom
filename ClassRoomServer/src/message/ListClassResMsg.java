package message;

import java.util.Collection;

public class ListClassResMsg extends AbstractMessage {
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
				+ wrapDataField(status.toString()) + SEPARATOR
				+ wrapDataField(classInfos.size()));
		for (final String classInfo : classInfos) {
			str.append(SEPARATOR + wrapDataField(classInfo));
		}
		str.append(SEPARATOR + END);
		return str.toString();
	}
}
