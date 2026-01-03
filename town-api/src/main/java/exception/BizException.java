package exception;

import java.io.Serializable;

public class BizException extends RuntimeException implements Serializable {

    private final int code;
    private final String msg;

    public BizException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BizException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
