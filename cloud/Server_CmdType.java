
public enum Server_CmdType {

    LOGIN_REQ,
    LOGOUT_REQ,
    CREATE_CLASS_REQ,
    LIST_CLASS_REQ,
    DEL_CLASS_REQ,
    JOIN_CLASS_REQ,
    QUERY_CLASS_INFO_REQ,
    QUIT_CLASS_REQ,
    KICK_USER_REQ,
    PUSH_CONTENT_REQ,
    PUSH_CONTENT_GET_REQ,
    COND_PUSH_CONTENT_REQ,
    GET_PRESENT_TOKEN_REQ,
    CHANGE_PRESENT_TOKEN_RES,
    RETRIEVE_PRESENT_TOKEN_REQ,
    
    INVALID_SERVER_CMD
}