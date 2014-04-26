package message;

import server.DeviceType;

public class LoginReqMsg extends AbstractMessage {
	private static final String head = "LOGIN_REQ";
	private final String userName;
	private final String password;
	private final DeviceType deviceType;
	// null for PC device type
	private final String tokenId;

	public LoginReqMsg(final String userName, final String password,
			final DeviceType deviceType, final String tokenId) {
		super();
		this.userName = userName;
		this.password = password;
		this.deviceType = deviceType;
		this.tokenId = tokenId;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public String getTokenId() {
		return tokenId;
	}

	@Override
	public String toMseeage() {
		throw new UnsupportedOperationException("Not to client");
		// return head + SEPARATOR + userName + SEPARATOR + password;
	}

	public static Message parse(final String message) {
		final String[] fields = message.split(SEPARATOR);
		if (fields.length < 4 || !head.equals(fields[0])) {
			return null;
		}
		if (!validDataField(fields)) {
			return null;
		}
		final DeviceType deviceType = DeviceType
				.getFromName(getData(fields[3]));
		if (deviceType == null) {
			return null;
		}
		final String tokenId;
		if (fields.length == 5) {
			tokenId = getData(fields[4]);
		} else {
			if (!DeviceType.PC.equals(deviceType)) {
				return null;
			}
			tokenId = null;
		}
		return new LoginReqMsg(getData(fields[1]), getData(fields[2]),
				deviceType, tokenId);
	}
}
