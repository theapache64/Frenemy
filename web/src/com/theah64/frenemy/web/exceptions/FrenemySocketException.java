package com.theah64.frenemy.web.exceptions;

/**
 * Created by theapache64 on 7/6/17.
 */
public class FrenemySocketException extends Exception {

    private final boolean isFatal;

    public FrenemySocketException(String message, boolean isFatal) {
        super(message);
        this.isFatal = isFatal;
    }

    public boolean isFatal() {
        return isFatal;
    }
}
