package message;

public class MessageFactory {
	private MessageFactory() {

	}

	public static Message parse(final String message) {
		try {
			Message msg;
			msg = LoginReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = CreateUsrReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			// msg = LoginResultMsg.parse(message);
			// if (msg != null) {
			// return msg;
			// }
			msg = LogoutReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = CreateClassReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = ListClassReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = DeleteClassReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = JoinClassReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = JoinClassApprovalResMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = QueryClassInfoReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = QuitClassReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = KickUserReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = PushContentReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			// msg = PushContentGetReqMsg.parse(message);
			// if (msg != null) {
			// return msg;
			// }
			msg = GetPresentTokenReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = ChangePresentTokenResMsg.parse(message);
			if (msg != null) {
				return msg;
			}
			msg = RetrivePresentTokenReqMsg.parse(message);
			if (msg != null) {
				return msg;
			}
		} catch (final Exception e) {
			throw new UnknownMessageException("Unknown message " + message);
		}
		throw new UnknownMessageException("Unknown message " + message);
	}
}
