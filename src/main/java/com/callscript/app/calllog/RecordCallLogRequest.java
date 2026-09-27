package com.callscript.app.calllog;

public record RecordCallLogRequest(String phase, String scriptId, String result, String ngReason) {
}
