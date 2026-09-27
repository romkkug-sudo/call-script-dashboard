package com.callscript.app.calllog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    List<CallLog> findByDayKeyOrderByTsDesc(String dayKey);

    List<CallLog> findAllByOrderByTsDesc();

    boolean existsByScriptId(String scriptId);
}
