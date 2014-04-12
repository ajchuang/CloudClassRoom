package message;

public interface Message {
	public static final String END="END";
	static final String SEPARATOR = "\n";
	static final String DATA_PREFIX=":";

	String toMseeage();
}
