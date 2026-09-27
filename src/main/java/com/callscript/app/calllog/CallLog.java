package com.callscript.app.calllog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * 架電ログ。1通話につき、フェーズ(受付突破/ごあいさつ/クロージング)ごとに別レコードとして記録する。
 */
@Entity
@Table(name = "call_logs")
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant ts;

    @Column(name = "day_key", nullable = false)
    private String dayKey;

    @Column(nullable = false)
    private String phase;

    @Column(name = "script_id", nullable = false)
    private String scriptId;

    @Column(name = "script_label", nullable = false)
    private String scriptLabel;

    @Column(nullable = false)
    private String result;

    @Column(name = "ng_reason")
    private String ngReason;

    private String memo;

    protected CallLog() {
        // JPA
    }

    public CallLog(Instant ts, String dayKey, String phase, String scriptId, String scriptLabel,
                   String result, String ngReason, String memo) {
        this.ts = ts;
        this.dayKey = dayKey;
        this.phase = phase;
        this.scriptId = scriptId;
        this.scriptLabel = scriptLabel;
        this.result = result;
        this.ngReason = ngReason;
        this.memo = memo;
    }

    public Long getId() {
        return id;
    }

    public Instant getTs() {
        return ts;
    }

    public String getDayKey() {
        return dayKey;
    }

    public String getPhase() {
        return phase;
    }

    public String getScriptId() {
        return scriptId;
    }

    public String getScriptLabel() {
        return scriptLabel;
    }

    public String getResult() {
        return result;
    }

    public String getNgReason() {
        return ngReason;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
