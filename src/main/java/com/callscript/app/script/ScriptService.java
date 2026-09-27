package com.callscript.app.script;

import com.callscript.app.calllog.CallLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScriptService {

    private static final Pattern VERSION_SUFFIX = Pattern.compile("\\s*v(\\d+)$");

    private final ScriptRepository repository;
    private final CallLogRepository callLogRepository;

    public ScriptService(ScriptRepository repository, CallLogRepository callLogRepository) {
        this.repository = repository;
        this.callLogRepository = callLogRepository;
    }

    public List<Script> listByPhase(String phase, boolean includeArchived) {
        List<Script> all = repository.findByPhaseOrderByCreatedAtAsc(phase);
        if (includeArchived) {
            return all;
        }
        return all.stream().filter(s -> !"archived".equals(s.getStatus())).toList();
    }

    @Transactional
    public Script create(String phase, String text) {
        int existingCount = repository.findByPhaseOrderByCreatedAtAsc(phase).size();
        String label = "案" + (existingCount + 1);
        Script script = new Script(newId(phase), phase, label, text, "active", Instant.now(), null);
        return repository.save(script);
    }

    @Transactional
    public Script revise(String existingId, String newText) {
        Script original = findOrThrow(existingId);

        // まだ一度も使われていないトークは、バージョンを増やさずそのまま上書きする
        // (過去の成績と混ざる心配がないため)。使用実績があるトークは、従来通り
        // 新バージョンとして発行し、旧バージョンをお蔵入りにする。
        if (!callLogRepository.existsByScriptId(existingId)) {
            original.setText(newText);
            return repository.save(original);
        }

        original.setStatus("archived");
        repository.save(original);

        String newLabel = nextVersionLabel(original.getLabel());
        Script revised = new Script(newId(original.getPhase()), original.getPhase(), newLabel,
                newText, "active", Instant.now(), original.getId());
        return repository.save(revised);
    }

    @Transactional
    public Script archive(String id) {
        Script script = findOrThrow(id);
        script.setStatus("archived");
        return repository.save(script);
    }

    @Transactional
    public Script restore(String id) {
        Script script = findOrThrow(id);
        script.setStatus("active");
        return repository.save(script);
    }

    private Script findOrThrow(String id) {
        return repository.findById(id).orElseThrow(() -> new ScriptNotFoundException(id));
    }

    private String newId(String phase) {
        return phase + "-" + System.currentTimeMillis();
    }

    private String nextVersionLabel(String currentLabel) {
        Matcher matcher = VERSION_SUFFIX.matcher(currentLabel);
        if (matcher.find()) {
            int nextVersion = Integer.parseInt(matcher.group(1)) + 1;
            String baseLabel = currentLabel.substring(0, matcher.start());
            return baseLabel + " v" + nextVersion;
        }
        return currentLabel + " v2";
    }
}
