package com.example.controller;

import nl.novi.LivingInSync.controller.CycleController;
import nl.novi.LivingInSync.dto.PhaseDto;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.model.Cycle;
import nl.novi.LivingInSync.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CycleController.class)
class CycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CycleService cycleService;

    @MockBean
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateCycleSuccess() throws Exception {
        CycleInputDto cycleInputDto = new CycleInputDto();
        cycleInputDto.setStartDate(LocalDate.of(2024, 1, 1));
        CycleOutputDto cycleOutputDto = new CycleOutputDto();
        cycleOutputDto.setId(1L);
        List<PhaseDto> phases = new ArrayList<>();
        cycleOutputDto.setPhases(phases);

        when(cycleService.createCycle(any(CycleInputDto.class), null)).thenReturn(cycleOutputDto);
        when(bindingResult.hasFieldErrors()).thenReturn(false);

        MvcResult result = mockMvc.perform(post("/cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2024-01-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/1")))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("\"id\":1"), "Response body should contain the ID of the created cycle");
    }

    @Test
    void testCreateCycleValidationError() throws Exception {
        when(bindingResult.hasFieldErrors()).thenReturn(true);
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("cycleInputDto", "startDate", "Start date is required"));
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MvcResult result = mockMvc.perform(post("/cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2024-01-01\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("startDate: Start date is required"), "Response body should contain validation error");
    }

    @Test
    void testUpdateCycle() throws Exception {
        CycleInputDto inputDto = new CycleInputDto();
        inputDto.setStartDate(LocalDate.of(2024, 2, 1));

        Cycle cycle = new Cycle();
        cycle.setStartDate(LocalDate.of(2024, 1, 1));
        when(cycleService.getCycle(any(Long.class))).thenReturn(new CycleOutputDto());

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            CycleInputDto arg = invocation.getArgument(1);
            if (id.equals(1L)) {
                cycle.setStartDate(arg.getStartDate());
            }
            return null;
        }).when(cycleService).updateCycle(any(Long.class), any(CycleInputDto.class));

        mockMvc.perform(put("/cycles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"startDate\": \"2024-02-01\"}"))
                .andExpect(status().isNoContent());

        verify(cycleService, times(1)).updateCycle(any(Long.class), any(CycleInputDto.class));
        assertEquals(LocalDate.of(2024, 2, 1), cycle.getStartDate(), "Cycle start date should be updated.");
    }

    @Test
    void testGetCycle() throws Exception {
        CycleOutputDto cycleOutputDto = new CycleOutputDto();
        cycleOutputDto.setId(1L);
        List<PhaseDto> phases = new ArrayList<>();
        cycleOutputDto.setPhases(phases);

        when(cycleService.getCycle(any(Long.class))).thenReturn(cycleOutputDto);

        MvcResult result = mockMvc.perform(get("/cycles/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("\"id\":1"), "Response body should contain the ID of the retrieved cycle");
    }
}
