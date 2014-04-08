package message;

public enum ClassAdminStatus {
	SUCCESS, NOT_LOGIN,
	/**
	 * Apply to Create class only
	 */
	DUPLICATE_NAME,
	/**
	 * Apply to delete class, join class, join class approval only
	 */
	INVALID_CLASS_ID,
	/**
	 * Apply to join class only
	 */
	JOIN_CLASS_DENIED,
	/**
	 * Apply to join class, add content(content already in class) only
	 */
	ALREADY_IN_CLASS,
	/**
	 * Apply to quit class, kick user from class
	 */
	NOT_IN_CLASS,
	/**
	 * Apply to get content only
	 */
	CONTENT_NOT_IN_CLASS,
	/**
	 * The client who request it is not permitted to to the specific action
	 */
	NO_PERMISSION,
	/**
	 * Apply to join class result
	 */
	DENIED,
	ALREADY_PRESENTER;
}
