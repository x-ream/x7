package io.xream.x7.base.exception;

public class DistributionLockException extends RuntimeException{

    private String message;

    public String getMessage(){
        return this.message;
    }

    public DistributionLockException(){
        super();
    }

    public DistributionLockException(String message){
        super();
        this.message = message;
    }
}
