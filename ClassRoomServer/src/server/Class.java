package server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Class {

	//public static final class ClassContent {
	//	private final String contentType;
	//	private final byte[] contents;

	//	public ClassContent(final String contentType, final byte[] contents) {
	//		super();
	//		this.contentType = contentType;
	//		this.contents = contents;
	//	}

	//	public String getContentType() {
	//		return contentType;
	//	}

	//	public byte[] getContents() {
	//		return contents;
	//	}
	//}

	private final long classId;
	private final String className;
	private final Instructor instructor;

	private final Map<String, Student> students;
	private User presenter;
	//content ids
	private final Set<Long> contents;

	public Class(final long classId, final String className,
			final Instructor creator) {
		this(classId, className, creator, new HashMap<String, Student>());
	}

	public Class(final long classId, final String className,
			final Instructor creator, final Map<String, Student> students) {
		this.classId = classId;
		this.className = className;
		instructor = creator;
		this.students = students;
		// default presenter is the creator
		presenter = instructor;
		contents = new HashSet<Long>();
	}

	public Instructor getInstructor() {
		return instructor;
	}

	/**
	 * 
	 * @return true if join successfully, false if the class already has this
	 *         student
	 */
	public boolean joinClass(final Student student) {
		// not null means the student is already in the class
		return students.put(student.getUserName(), student) == null;
	}

	public boolean inClass(final String studentName) {
		return students.containsKey(studentName);
	}

	/**
	 * 
	 * @return if the student to leave was in the class
	 */
	public boolean leaveClass(final Student student) {
		// null means no such student
		return students.remove(student.getUserName()) != null;
	}

	/**
	 * 
	 * @return false if the class doesn't contain the new presenter
	 */
	public boolean assignPresenter(final User newPresenter) {
		return assignPresenter(newPresenter.getUserName());
	}

	public boolean assignPresenter(final String newPresenter) {
		System.out.println("new presenter " + newPresenter);

		if (instructor.getUserName().equals(newPresenter)) {
			presenter = instructor;
			return true;
		}
		System.out.println("Presenter is not instructor");
		final Student s = students.get(newPresenter);
		if (s != null) {
			presenter = s;
			return true;
		}
		return false;
	}

	public boolean hasContent(final long contentId) {
		return contents.contains(contentId);
	}

	public void pushContent(final long contentId) {
		contents.add(contentId);
	}

	//public ClassContent getContent(final long contentId) {
	//	return contents.get(contentId);
	//}

	public Collection<Student> getStudents() {
		return students.values();
	}

	public User getPresenter() {
		return presenter;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	@Override
	public String toString() {
		final StringBuilder studentList = new StringBuilder();
		for (final Student s : students.values()) {
			studentList.append(s.userName + " ");
		}
		return "Class [classId=" + classId + ", className=" + className
				+ ", instructor=" + instructor.userName + ", students=["
				+ studentList + "], presenter=" + presenter.getUserName() + "]";
	}
}
