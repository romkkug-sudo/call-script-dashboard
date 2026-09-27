package com.callscript.app.script;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/scripts")
public class ScriptController {

    private final ScriptService service;

    public ScriptController(ScriptService service) {
        this.service = service;
    }

    @GetMapping
    public List<ScriptResponse> getScripts(@RequestParam String phase,
                                            @RequestParam(defaultValue = "false") boolean includeArchived) {
        return service.listByPhase(phase, includeArchived).stream().map(ScriptResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<ScriptResponse> createScript(@RequestBody CreateScriptRequest request) {
        Script created = service.create(request.phase(), request.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(ScriptResponse.from(created));
    }

    @PostMapping("/{id}/revise")
    public ScriptResponse revise(@PathVariable String id, @RequestBody ReviseScriptRequest request) {
        return ScriptResponse.from(service.revise(id, request.text()));
    }

    @PatchMapping("/{id}/archive")
    public ScriptResponse archive(@PathVariable String id) {
        return ScriptResponse.from(service.archive(id));
    }

    @PatchMapping("/{id}/restore")
    public ScriptResponse restore(@PathVariable String id) {
        return ScriptResponse.from(service.restore(id));
    }
}
