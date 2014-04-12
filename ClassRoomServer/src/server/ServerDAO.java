package server;

import java.util.List;
import java.util.Map;

public interface ServerDAO {
	List<User> loadUsers();

	Map<Long, Class> loadClasses();

	void insertNewClass(final Class c);

	void deleteClass(final long classId);

	void addStudentToClass(final long classId, final String student);

	void leaveClass(final long classId, final String student);

	void assignPresenter(long classId, String presenter);
}
