package com.callscript.app.script;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScriptRepository extends JpaRepository<Script, String> {

    List<Script> findByPhaseOrderByCreatedAtAsc(String phase);
}
