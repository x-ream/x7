package x7.core.exception;

/**
 * Created by Sim on 2018/6/22.
 */
public class BizException extends RuntimeException {

    private String message;

    public BizException(){

    }

    public BizException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
