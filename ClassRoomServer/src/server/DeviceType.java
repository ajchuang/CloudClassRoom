package server;

public enum DeviceType {

	CELL("iOS", true), DESKTOP("PC", false);

	private final String name;
	private final boolean pushNotification;

	private DeviceType(final String name, final boolean pushNotification) {
		this.name = name;
		this.pushNotification = pushNotification;
	}

	public String getName() {
		return name;
	}

	public boolean isPushNotification() {
		return pushNotification;
	}

	public static DeviceType getFromName(final String name) {
		for (final DeviceType t : DeviceType.values()) {
			if (t.name.equals(name)) {
				return t;
			}
		}
		return null;
	}
}
