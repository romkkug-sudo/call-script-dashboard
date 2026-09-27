package com.callscript.app.calllog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class CallLogController {

    private final CallLogService service;

    public CallLogController(CallLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<CallLogResponse> getLogs(@RequestParam(required = false) String day) {
        List<CallLog> logs = "today".equals(day) ? service.listForToday() : service.listAll();
        return logs.stream().map(CallLogResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<CallLogResponse> record(@RequestBody RecordCallLogRequest request) {
        CallLog created = service.record(request.phase(), request.scriptId(), request.result(), request.ngReason());
        return ResponseEntity.status(HttpStatus.CREATED).body(CallLogResponse.from(created));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/memo")
    public CallLogResponse updateMemo(@PathVariable Long id, @RequestBody UpdateMemoRequest request) {
        return CallLogResponse.from(service.updateMemo(id, request.memo()));
    }
}
