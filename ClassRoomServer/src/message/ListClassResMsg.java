package message;

import java.util.Collection;

import server.Class;

public class ListClassResMsg extends AbstractMessage {
	private static final String head = "LIST_CLASS_RES";

	private final QueryResultStatus status;
	private final Collection<Class> classInfos;

	public ListClassResMsg(final QueryResultStatus status,
			final Collection<Class> classInfos) {
		super();
		this.status = status;
		this.classInfos = classInfos;
	}

	@Override
	public String toMseeage() {
		final StringBuilder str = new StringBuilder(head + SEPARATOR
				+ wrapDataField(status.toString()) + SEPARATOR
				+ wrapDataField(classInfos.size()));
		for (final Class c : classInfos) {
			str.append(SEPARATOR + wrapDataField(c.getClassId())
					+ Message.SEPARATOR + wrapDataField(c.getClassName())
					+ Message.SEPARATOR
					+ wrapDataField(c.getInstructor().getUserName()));
		}
		str.append(SEPARATOR + END);
		return str.toString();
	}
}
