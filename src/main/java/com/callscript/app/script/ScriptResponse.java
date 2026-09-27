package com.callscript.app.script;

import java.time.Instant;

public record ScriptResponse(String id, String phase, String label, String text, String status,
                              Instant createdAt, String revisionOf) {

    public static ScriptResponse from(Script script) {
        return new ScriptResponse(script.getId(), script.getPhase(), script.getLabel(), script.getText(),
                script.getStatus(), script.getCreatedAt(), script.getRevisionOf());
    }
}
