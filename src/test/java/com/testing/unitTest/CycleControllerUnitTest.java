package com.testing.unitTest;

import nl.novi.LivingInSync.LivingInSyncApplication;
import nl.novi.LivingInSync.controller.CycleController;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.security.CustomUserDetailsService;
import nl.novi.LivingInSync.security.JwtService;
import nl.novi.LivingInSync.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CycleController.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {LivingInSyncApplication.class, CycleController.class})
class CycleControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CycleService cycleService;

    @MockBean
    JwtService jwtService;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        Mockito.reset(cycleService, jwtService, customUserDetailsService);
    }

    @Test
    @WithMockUser(username="testuser", roles="USER")
    void testCreateCycleSuccess() throws Exception {
        CycleOutputDto cycleOutputDto = new CycleOutputDto(1L, new ArrayList<>());

        when(cycleService.createOrUpdateCycle(any(CycleInputDto.class), any())).thenReturn(cycleOutputDto);

        String requestJson = """
            {
                "startDate": "2024-01-01"
            }
            """;

        mockMvc.perform(post("/cycles")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/cycles"))) // Updated to match "/cycles"
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @WithMockUser(username="testuser", roles="USER")
    void testUpdateCycleSuccess() throws Exception {
        CycleOutputDto cycleOutputDto = new CycleOutputDto(1L, new ArrayList<>());

        when(cycleService.updateCycleForUser(any(CycleInputDto.class), any())).thenReturn(cycleOutputDto);

        String requestJson = """
            {
                "startDate": "2024-02-01"
            }
            """;

        mockMvc.perform(put("/cycles")
                        .with(csrf())
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.phases").exists());
    }

    @Test
    @WithMockUser(username="testuser", roles="USER")
    void testGetCycleSuccess() throws Exception {
        CycleOutputDto cycleOutputDto = new CycleOutputDto(1L, new ArrayList<>());

        when(cycleService.getUserCycle(any())).thenReturn(cycleOutputDto);

        mockMvc.perform(get("/cycles")) // Updated to match the correct URL mapping
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.phases").exists());
    }
}
