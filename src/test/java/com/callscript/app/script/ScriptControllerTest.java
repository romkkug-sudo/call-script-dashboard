package com.callscript.app.script;

import com.callscript.app.common.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScriptController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class ScriptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ScriptService service;

    @Test
    void getScripts_フェーズを指定すると該当スクリプト一覧を返す() throws Exception {
        Script script = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "active", Instant.now(), null);
        when(service.listByPhase("uketsuke", false)).thenReturn(List.of(script));

        mockMvc.perform(get("/api/scripts").param("phase", "uketsuke"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("uketsuke-1"))
                .andExpect(jsonPath("$[0].label").value("基本形"));
    }

    @Test
    void getScripts_includeArchivedをtrueにするとサービスにtrueで渡す() throws Exception {
        when(service.listByPhase("uketsuke", true)).thenReturn(List.of());

        mockMvc.perform(get("/api/scripts").param("phase", "uketsuke").param("includeArchived", "true"))
                .andExpect(status().isOk());

        verify(service).listByPhase(eq("uketsuke"), eq(true));
    }

    @Test
    void postScripts_新規追加すると201でスクリプトを返す() throws Exception {
        Script created = new Script("uketsuke-999", "uketsuke", "案1", "新しい本文", "active", Instant.now(), null);
        when(service.create("uketsuke", "新しい本文")).thenReturn(created);

        mockMvc.perform(post("/api/scripts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("phase", "uketsuke", "text", "新しい本文"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uketsuke-999"))
                .andExpect(jsonPath("$.label").value("案1"));
    }

    @Test
    void postRevise_編集すると新バージョンを返す() throws Exception {
        Script revised = new Script("uketsuke-999", "uketsuke", "基本形 v2", "新本文", "active", Instant.now(), "uketsuke-1");
        when(service.revise("uketsuke-1", "新本文")).thenReturn(revised);

        mockMvc.perform(post("/api/scripts/uketsuke-1/revise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("text", "新本文"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label").value("基本形 v2"))
                .andExpect(jsonPath("$.revisionOf").value("uketsuke-1"));
    }

    @Test
    void postRevise_存在しないidを指定すると404を返す() throws Exception {
        when(service.revise(eq("missing"), org.mockito.ArgumentMatchers.anyString()))
                .thenThrow(new ScriptNotFoundException("missing"));

        mockMvc.perform(post("/api/scripts/missing/revise")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("text", "本文"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchArchive_お蔵入りにする() throws Exception {
        Script archived = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "archived", Instant.now(), null);
        when(service.archive("uketsuke-1")).thenReturn(archived);

        mockMvc.perform(patch("/api/scripts/uketsuke-1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("archived"));
    }

    @Test
    void patchRestore_復活させる() throws Exception {
        Script restored = new Script("uketsuke-1", "uketsuke", "基本形", "本文", "active", Instant.now(), null);
        when(service.restore("uketsuke-1")).thenReturn(restored);

        mockMvc.perform(patch("/api/scripts/uketsuke-1/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("active"));
    }
}
