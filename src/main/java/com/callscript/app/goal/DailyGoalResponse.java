package com.callscript.app.goal;

public record DailyGoalResponse(int callTarget, int appointmentTarget) {

    public static DailyGoalResponse from(DailyGoal goal) {
        return new DailyGoalResponse(goal.getCallTarget(), goal.getAppointmentTarget());
    }
}
