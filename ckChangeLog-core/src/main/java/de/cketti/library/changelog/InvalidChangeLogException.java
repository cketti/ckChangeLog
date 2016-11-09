package de.cketti.library.changelog;


public class InvalidChangeLogException extends RuntimeException {
    public InvalidChangeLogException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidChangeLogException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
