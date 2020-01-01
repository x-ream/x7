package io.xream.x7.exception;

public class CacheException extends RuntimeException{

    private String message;

    public CacheException(){

    }

    public CacheException(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
