package io.github.ramdathdylan.brickreviews.auth;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message){
        super(message);
    }
    
}
