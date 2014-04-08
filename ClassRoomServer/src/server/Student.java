package server;

public class Student extends AbstractUser {
	public Student(final String userName, final String password) {
		super(userName, password);
	}
	
	@Override
	public String toString() {
		return "Student [userName=" + userName + ", password=" + password + "]";
	}
}
