package com.callscript.app.calllog;

import com.callscript.app.common.DayKeys;
import com.callscript.app.script.Script;
import com.callscript.app.script.ScriptNotFoundException;
import com.callscript.app.script.ScriptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CallLogService {

    private final CallLogRepository repository;
    private final ScriptRepository scriptRepository;

    public CallLogService(CallLogRepository repository, ScriptRepository scriptRepository) {
        this.repository = repository;
        this.scriptRepository = scriptRepository;
    }

    @Transactional
    public CallLog record(String phase, String scriptId, String result, String ngReason) {
        Script script = scriptRepository.findById(scriptId)
                .orElseThrow(() -> new ScriptNotFoundException(scriptId));

        CallLog log = new CallLog(Instant.now(), DayKeys.today(), phase, scriptId,
                script.getLabel(), result, ngReason, "");
        return repository.save(log);
    }

    public List<CallLog> listForToday() {
        return repository.findByDayKeyOrderByTsDesc(DayKeys.today());
    }

    public List<CallLog> listAll() {
        return repository.findAllByOrderByTsDesc();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public CallLog updateMemo(Long id, String memo) {
        CallLog log = repository.findById(id).orElseThrow(() -> new CallLogNotFoundException(id));
        log.setMemo(memo);
        return repository.save(log);
    }
}
