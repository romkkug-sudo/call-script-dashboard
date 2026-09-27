package com.callscript.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * トーク追加 → 通話記録 → 一覧取得 → お蔵入り、という一連の流れを実際のH2(インメモリ)で通す結合テスト。
 */
@SpringBootTest
@AutoConfigureMockMvc
class CallScriptFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void トーク追加から通話記録_お蔵入りまでの一連の流れが動作する() throws Exception {
        String createBody = mockMvc.perform(post("/api/scripts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("phase", "uketsuke", "text", "結合テスト用トーク"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("active"))
                .andReturn().getResponse().getContentAsString();

        String scriptId = objectMapper.readTree(createBody).get("id").asText();

        mockMvc.perform(get("/api/scripts").param("phase", "uketsuke"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + scriptId + "')]").isNotEmpty());

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("phase", "uketsuke", "scriptId", scriptId, "result", "success"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scriptLabel").value(org.hamcrest.Matchers.startsWith("案")));

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.scriptId=='" + scriptId + "')]").isNotEmpty());

        mockMvc.perform(patch("/api/scripts/" + scriptId + "/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("archived"));

        mockMvc.perform(get("/api/scripts").param("phase", "uketsuke"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + scriptId + "')]").isEmpty());

        mockMvc.perform(get("/api/scripts").param("phase", "uketsuke").param("includeArchived", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + scriptId + "')]").isNotEmpty());
    }
}
