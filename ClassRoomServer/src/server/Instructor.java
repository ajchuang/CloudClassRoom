package server;

public class Instructor extends AbstractUser {

	public Instructor(final String userName, final String password) {
		super(userName, password);
	}
	
	@Override
	public String toString() {
		return "Instructor [userName=" + userName + ", password=" + password
				+ "]";
	}
}
