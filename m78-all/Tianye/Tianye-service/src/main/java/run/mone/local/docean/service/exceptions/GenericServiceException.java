package run.mone.local.docean.service.exceptions;

import lombok.Getter;

@Getter
public class GenericServiceException extends RuntimeException {

    private int code;

    public GenericServiceException(String message) {
        super(message);
        this.code = -1;
    }

    public GenericServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GenericServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}


