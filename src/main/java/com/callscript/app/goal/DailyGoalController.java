package com.callscript.app.goal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goal")
public class DailyGoalController {

    private final DailyGoalService service;

    public DailyGoalController(DailyGoalService service) {
        this.service = service;
    }

    @GetMapping
    public DailyGoalResponse getGoal() {
        return DailyGoalResponse.from(service.getGoal());
    }

    @PutMapping
    public DailyGoalResponse updateGoal(@RequestBody UpdateDailyGoalRequest request) {
        return DailyGoalResponse.from(service.update(request.callTarget(), request.appointmentTarget()));
    }
}
