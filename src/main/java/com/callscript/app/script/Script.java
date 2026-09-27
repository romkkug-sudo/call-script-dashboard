package com.callscript.app.script;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * トークマスタ。使用実績(CallLog)があるスクリプトを編集すると、元のレコードは archived になり
 * 新しい id で新バージョンが作られる(差し替えではなく新バージョン発行、過去の成績は旧 id に
 * 紐づいたまま残る)。まだ使用実績がないスクリプトは、同じ id のまま上書き編集される。
 */
@Entity
@Table(name = "scripts")
public class Script {

    @Id
    private String id;

    @Column(nullable = false)
    private String phase;

    @Column(nullable = false)
    private String label;

    @Lob
    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "revision_of")
    private String revisionOf;

    protected Script() {
        // JPA
    }

    public Script(String id, String phase, String label, String text, String status,
                  Instant createdAt, String revisionOf) {
        this.id = id;
        this.phase = phase;
        this.label = label;
        this.text = text;
        this.status = status;
        this.createdAt = createdAt;
        this.revisionOf = revisionOf;
    }

    public String getId() {
        return id;
    }

    public String getPhase() {
        return phase;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getRevisionOf() {
        return revisionOf;
    }
}
