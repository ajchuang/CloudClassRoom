package message;

public class CreateUsrReqMsg extends AbstractMessage {
	
	private static final String head = "NEW_USER_REQ";
	private final String userName;
	private final String password;
	private final String role;
	
	public CreateUsrReqMsg(final String userName, final String password, final String role){
		this.userName = userName;
		this.password = password;
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}


	public String getPassword() {
		return password;
	}


	public String getRole() {
		return role;
	}

	@Override
	public String toMseeage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Message parse(final String message){
		final String[] fields = message.split(SEPARATOR);
		if(fields.length != 4 || !head.equals(fields[0])){
			return null;
		}
		if(!validDataField(fields)){
			return null;
		}
		if(!getData(fields[3]).equals("Student") && !getData(fields[3]).equals("Instructor")){
			return null;
		}
		return new CreateUsrReqMsg(getData(fields[1]), getData(fields[2]), getData(fields[3]));
	}

}
