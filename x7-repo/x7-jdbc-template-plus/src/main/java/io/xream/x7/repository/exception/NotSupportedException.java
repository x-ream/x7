package io.xream.x7.repository.exception;

public class NotSupportedException extends RuntimeException{

    private static final long serialVersionUID = 5749142995896243581L;
    private String message;

    public NotSupportedException(){

    }

    public NotSupportedException(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
