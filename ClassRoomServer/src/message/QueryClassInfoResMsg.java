package message;

import java.util.Collection;

public class QueryClassInfoResMsg extends AbstractMessage {
	private static final String head = "QUERY_CLASS_INFO_RES";
	private final String status;
	private final String instructor;
	private final int numOfStudent;
	private final Collection<String> students;

	public QueryClassInfoResMsg(String status, String instructor,
			Collection<String> students) {
		super();
		this.status = status;
		this.instructor = instructor;
		this.numOfStudent = students.size();
		this.students = students;
	}

	@Override
	public String toMseeage() {
		final StringBuilder str = new StringBuilder(head + SEPARATOR
				+ wrapDataField(status) + SEPARATOR + wrapDataField(instructor)
				+ SEPARATOR + wrapDataField(numOfStudent));
		for (final String student : students) {
			str.append(SEPARATOR + wrapDataField(student));
		}
		str.append(SEPARATOR + END);
		return str.toString();
	}
}
