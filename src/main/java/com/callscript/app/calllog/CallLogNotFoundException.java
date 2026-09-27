package com.callscript.app.calllog;

public class CallLogNotFoundException extends RuntimeException {

    public CallLogNotFoundException(Long id) {
        super("call log not found: " + id);
    }
}
