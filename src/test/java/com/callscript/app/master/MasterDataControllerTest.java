package com.callscript.app.master;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MasterDataController.class)
class MasterDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPhases_4フェーズの定義を返す() throws Exception {
        mockMvc.perform(get("/api/phases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key").value("uketsuke"))
                .andExpect(jsonPath("$[0].label").value("受付突破"))
                .andExpect(jsonPath("$[0].results.length()").value(3))
                .andExpect(jsonPath("$[0].results[0].key").value("success"))
                .andExpect(jsonPath("$[0].results[1].key").value("absent"))
                .andExpect(jsonPath("$[0].results[1].label").value("担当者不在"))
                .andExpect(jsonPath("$[0].results[2].key").value("receptionNg"))
                .andExpect(jsonPath("$[0].results[2].label").value("受付NG"))
                .andExpect(jsonPath("$[0].successKeys[0]").value("success"))
                .andExpect(jsonPath("$[2].key").value("closing"))
                .andExpect(jsonPath("$[2].results.length()").value(5))
                .andExpect(jsonPath("$[3].key").value("kirikaeshi"))
                .andExpect(jsonPath("$[3].label").value("切り返し"))
                .andExpect(jsonPath("$[3].results.length()").value(0))
                .andExpect(jsonPath("$[3].successKeys.length()").value(0));
    }

    @Test
    void getNgReasons_固定6件のNG理由を返す() throws Exception {
        mockMvc.perform(get("/api/ng-reasons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0]").value("今は必要ない"));
    }
}
