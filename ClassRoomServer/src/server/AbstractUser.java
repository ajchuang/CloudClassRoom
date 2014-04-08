package server;

public abstract class AbstractUser implements User {

	protected final String userName;
	protected final String password;

	public AbstractUser(final String userName, final String password) {
		super();
		this.userName = userName;
		this.password = password;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getPassword() {
		return password;
	}
}