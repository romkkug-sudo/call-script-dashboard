package com.callscript.app.calllog;

import java.time.Instant;

public record CallLogResponse(Long id, Instant ts, String dayKey, String phase, String scriptId,
                               String scriptLabel, String result, String ngReason, String memo) {

    public static CallLogResponse from(CallLog log) {
        return new CallLogResponse(log.getId(), log.getTs(), log.getDayKey(), log.getPhase(), log.getScriptId(),
                log.getScriptLabel(), log.getResult(), log.getNgReason(), log.getMemo());
    }
}
