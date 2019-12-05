package x7.core.exception;

/**
 * Created by Sim on 2018/6/22.
 */
public class RemoteServiceException extends RuntimeException {

    private String message;

    public RemoteServiceException(){

    }

    public RemoteServiceException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
