package com.callscript.app.master;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MasterDataController {

    @GetMapping("/phases")
    public List<PhaseDefinition> getPhases() {
        return Phases.ALL;
    }

    @GetMapping("/ng-reasons")
    public List<String> getNgReasons() {
        return NgReasons.ALL;
    }
}
