package com.callscript.app.master;

import java.util.List;

public record PhaseDefinition(String key, String label, List<ResultOption> results, List<String> successKeys) {
}
