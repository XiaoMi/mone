package com.xiaomi.data.push.antlr.sql.exceptions;

public class SqlParseException extends Exception {

    public SqlParseException(Exception e) {
        super(e);
    }

    public SqlParseException(String e) {
        super(e);
    }

    public SqlParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
