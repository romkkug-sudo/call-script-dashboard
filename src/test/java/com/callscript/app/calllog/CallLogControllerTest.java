package com.callscript.app.calllog;

import com.callscript.app.common.DayKeys;
import com.callscript.app.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CallLogController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class CallLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CallLogService service;

    @Test
    void getLogs_dayパラメータがtodayなら当日分のみ返す() throws Exception {
        CallLog log = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "");
        when(service.listForToday()).thenReturn(List.of(log));

        mockMvc.perform(get("/api/logs").param("day", "today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phase").value("uketsuke"))
                .andExpect(jsonPath("$[0].scriptLabel").value("基本形"));
    }

    @Test
    void getLogs_dayパラメータなしなら全期間のログを返す() throws Exception {
        CallLog log = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "");
        when(service.listAll()).thenReturn(List.of(log));

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phase").value("uketsuke"));
    }

    @Test
    void postLogs_記録すると201でログを返す() throws Exception {
        CallLog created = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "ng", "担当者不在", "");
        when(service.record("uketsuke", "uketsuke-1", "ng", "担当者不在")).thenReturn(created);

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "phase", "uketsuke", "scriptId", "uketsuke-1",
                                "result", "ng", "ngReason", "担当者不在"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result").value("ng"))
                .andExpect(jsonPath("$.ngReason").value("担当者不在"));
    }

    @Test
    void deleteLogs_指定したidを削除して204を返す() throws Exception {
        mockMvc.perform(delete("/api/logs/5"))
                .andExpect(status().isNoContent());

        verify(service).delete(5L);
    }

    @Test
    void patchMemo_メモを更新して200で返す() throws Exception {
        CallLog updated = new CallLog(Instant.now(), DayKeys.today(), "uketsuke", "uketsuke-1", "基本形", "success", null, "いい感触だった");
        when(service.updateMemo(eq(5L), eq("いい感触だった"))).thenReturn(updated);

        mockMvc.perform(patch("/api/logs/5/memo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("memo", "いい感触だった"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memo").value("いい感触だった"));
    }

    @Test
    void postLogs_存在しないscriptIdを指定すると404を返す() throws Exception {
        when(service.record(eq("uketsuke"), eq("missing"), eq("success"), eq((String) null)))
                .thenThrow(new com.callscript.app.script.ScriptNotFoundException("missing"));

        mockMvc.perform(post("/api/logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "phase", "uketsuke", "scriptId", "missing", "result", "success"))))
                .andExpect(status().isNotFound());
    }
}
