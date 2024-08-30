package com.example.integrationTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.service.CycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest(classes = LivingInSyncApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CycleControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    CycleService cycleService;

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void shouldCreateCorrectCycle() throws Exception {

        String requestJson = """
                {
                    "startDate" : "2024-08-16"
                }
                """;

        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/cycles")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
}

