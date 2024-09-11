package com.testing.integrationTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
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
class CycleIntegrationTest {

    @Autowired
    MockMvc mockMvc;

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
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phases").exists());
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void shouldRetrieveUserCycle() throws Exception {
        // getting an already existing cycle
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/cycles")
                        .contentType(APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phases").exists());
    }

    @Test
    @WithMockUser(username = "test", roles = {"USER"})
    void shouldUpdateCycleSuccessfully() throws Exception {
        String requestJson = """
                {
                    "startDate" : "2024-09-01"
                }
                """;

        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/cycles")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.phases[0].startDate").value("2024-09-01"));
    }
}
