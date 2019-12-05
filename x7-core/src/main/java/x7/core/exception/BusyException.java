package x7.core.exception;

public class BusyException extends RuntimeException {

    private String message;
    public BusyException(String message) {
        this.message  = message;
    }

    public String getMessage(){
        return this.message;
    }

}
