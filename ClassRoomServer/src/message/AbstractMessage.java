package message;

public abstract class AbstractMessage implements Message {

	static String getData(final String dataField) {
		// skip data field prefix
		return dataField.substring(1);
	}

	static boolean validDataField(final String[] fields) {
		for (int i = 1; i < fields.length; i++) {
			if (!fields[i].startsWith(DATA_PREFIX)) {
				return false;
			}
		}
		return true;
	}

	public String wrapDataField(final Object data) {
		return DATA_PREFIX + data;
	}
}
