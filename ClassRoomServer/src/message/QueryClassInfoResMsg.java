package message;

import java.util.Collection;

public class QueryClassInfoResMsg implements Message {
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
		final StringBuilder str = new StringBuilder(head + SEPARATOR + status
				+ SEPARATOR + instructor + SEPARATOR + numOfStudent);
		for (final String student : students) {
			str.append(SEPARATOR + student);
		}
		return str.toString();
	}
}
