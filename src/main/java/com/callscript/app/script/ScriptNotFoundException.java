package com.callscript.app.script;

public class ScriptNotFoundException extends RuntimeException {

    public ScriptNotFoundException(String id) {
        super("script not found: " + id);
    }
}
