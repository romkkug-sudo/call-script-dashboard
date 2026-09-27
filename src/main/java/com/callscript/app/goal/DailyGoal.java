package com.callscript.app.goal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 1日の目標値(コール数・アポ数)。今は個人利用前提のため、設定は1行のみ持つ。
 */
@Entity
@Table(name = "daily_goal")
public class DailyGoal {

    @Id
    private String id;

    @Column(name = "call_target", nullable = false)
    private int callTarget;

    @Column(name = "appointment_target", nullable = false)
    private int appointmentTarget;

    protected DailyGoal() {
        // JPA
    }

    public DailyGoal(String id, int callTarget, int appointmentTarget) {
        this.id = id;
        this.callTarget = callTarget;
        this.appointmentTarget = appointmentTarget;
    }

    public String getId() {
        return id;
    }

    public int getCallTarget() {
        return callTarget;
    }

    public void setCallTarget(int callTarget) {
        this.callTarget = callTarget;
    }

    public int getAppointmentTarget() {
        return appointmentTarget;
    }

    public void setAppointmentTarget(int appointmentTarget) {
        this.appointmentTarget = appointmentTarget;
    }
}
