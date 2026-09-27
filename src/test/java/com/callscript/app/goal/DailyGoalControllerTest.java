package com.callscript.app.goal;

import com.callscript.app.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DailyGoalController.class)
@Import(GlobalExceptionHandler.class)
class DailyGoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DailyGoalService service;

    @Test
    void getGoal_現在の目標値を返す() throws Exception {
        when(service.getGoal()).thenReturn(new DailyGoal("default", 70, 1));

        mockMvc.perform(get("/api/goal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callTarget").value(70))
                .andExpect(jsonPath("$.appointmentTarget").value(1));
    }

    @Test
    void putGoal_目標値を更新して返す() throws Exception {
        when(service.update(100, 3)).thenReturn(new DailyGoal("default", 100, 3));

        mockMvc.perform(put("/api/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("callTarget", 100, "appointmentTarget", 3))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.callTarget").value(100))
                .andExpect(jsonPath("$.appointmentTarget").value(3));
    }

    @Test
    void putGoal_不正な値だと400を返す() throws Exception {
        when(service.update(0, 1)).thenThrow(new IllegalArgumentException("callTarget must be positive: 0"));

        mockMvc.perform(put("/api/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("callTarget", 0, "appointmentTarget", 1))))
                .andExpect(status().isBadRequest());
    }
}
