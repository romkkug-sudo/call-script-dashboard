package com.callscript.app.goal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DailyGoalService {

    static final String GOAL_ID = "default";
    static final int DEFAULT_CALL_TARGET = 70;
    static final int DEFAULT_APPOINTMENT_TARGET = 1;

    private final DailyGoalRepository repository;

    public DailyGoalService(DailyGoalRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DailyGoal getGoal() {
        return repository.findById(GOAL_ID)
                .orElseGet(() -> repository.save(
                        new DailyGoal(GOAL_ID, DEFAULT_CALL_TARGET, DEFAULT_APPOINTMENT_TARGET)));
    }

    @Transactional
    public DailyGoal update(int callTarget, int appointmentTarget) {
        if (callTarget <= 0) {
            throw new IllegalArgumentException("callTarget must be positive: " + callTarget);
        }
        if (appointmentTarget <= 0) {
            throw new IllegalArgumentException("appointmentTarget must be positive: " + appointmentTarget);
        }
        DailyGoal goal = repository.findById(GOAL_ID)
                .orElseGet(() -> new DailyGoal(GOAL_ID, callTarget, appointmentTarget));
        goal.setCallTarget(callTarget);
        goal.setAppointmentTarget(appointmentTarget);
        return repository.save(goal);
    }
}
