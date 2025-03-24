package com.tokioschool.ratingapp.core.exceptions;

public class OperationNotAllowException extends RuntimeException {
    public OperationNotAllowException(Throwable cause){super(cause);}
    public OperationNotAllowException(String message) {
        super(message);
    }
    public OperationNotAllowException(String message, Throwable cause){super(message,cause);}
    public OperationNotAllowException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {super(message, cause, enableSuppression, writableStackTrace);}
}
