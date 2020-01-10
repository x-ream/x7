package io.xream.x7.exception;

public class L2CacheException extends RuntimeException{

    private String message;

    public L2CacheException(){

    }

    public L2CacheException(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
