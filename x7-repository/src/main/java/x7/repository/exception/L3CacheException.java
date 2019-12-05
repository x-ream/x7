package x7.repository.exception;

public class L3CacheException extends RuntimeException{

    private String message;

    public L3CacheException(){

    }

    public L3CacheException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
