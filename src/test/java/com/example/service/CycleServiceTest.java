package com.example.service;

import nl.novi.LivingInSync.dto.PhaseDto;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.Cycle;
import nl.novi.LivingInSync.model.Follicular;
import nl.novi.LivingInSync.model.Menstruation;
import nl.novi.LivingInSync.model.Ovulation;
import nl.novi.LivingInSync.repository.CycleRepository;
import nl.novi.LivingInSync.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CycleServiceTest {

    @Mock
    private CycleRepository cycleRepository;

    @InjectMocks
    private CycleService cycleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreatePhases() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        List<PhaseDto> phases = cycleService.createPhases(startDate);

        // Ensure phases are created and the list is not null
        assertNotNull(phases, "Phases list should not be null.");
        assertEquals(52, phases.size(), "There should be 52 phases for a full cycle year.");

        // Print all phases with their dates and types
        System.out.println("All Phases:");
        phases.forEach(phase ->
                System.out.println("Phase: " + phase.getStartDate() + " to " + phase.getEndDate() + ": " + phase.getClass().getSimpleName())
        );

        // Define an array of test cases with dates and expected phases
        Object[][] testCases = {
                {LocalDate.of(2024, 1, 5), Menstruation.class},
                {LocalDate.of(2024, 3, 11), Ovulation.class},
                {LocalDate.of(2024, 4, 22), Menstruation.class},
                {LocalDate.of(2024, 6, 1), Follicular.class},
        };

        Stream.of(testCases).forEach(testCase -> {
            LocalDate date = (LocalDate) testCase[0];
            Class<?> expectedPhaseClass = (Class<?>) testCase[1];

            System.out.println("Testing date: " + date);

            PhaseDto foundPhase = phases.stream()
                    .filter(phase -> {
                        System.out.println("Checking phase: " + phase.getStartDate() + " to " + phase.getEndDate());
                        return !date.isBefore(phase.getStartDate()) && !date.isAfter(phase.getEndDate());
                    })
                    .findFirst()
                    .orElse(null);

            assertNotNull(foundPhase, "Phase should be found for date: " + date);
            assertTrue(expectedPhaseClass.isInstance(foundPhase),
                    "On " + date + " you should be in the " + expectedPhaseClass.getSimpleName() + " phase but was in " + (foundPhase != null ? foundPhase.getClass().getSimpleName() : "null") + " phase!");
        });
    }

    @Test
    void testCreateCycle() {
        CycleInputDto inputDto = new CycleInputDto();
        inputDto.setStartDate(LocalDate.of(2024, 4, 26));

        Cycle cycle = new Cycle();
        cycle.setStartDate(inputDto.getStartDate());
        when(cycleRepository.save(any(Cycle.class))).thenReturn(cycle);

        CycleOutputDto outputDto = cycleService.createCycle(inputDto);

        assertNotNull(outputDto, "Output DTO should not be null.");
        assertEquals(52, outputDto.getPhases().size(), "There should be 52 phases for a full cycle year.");
    }

    @Test
    void testGetCycle() {
        Cycle cycle = new Cycle();
        cycle.setStartDate(LocalDate.of(2024, 1, 1));
        when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

        CycleOutputDto outputDto = cycleService.getCycle(1L);

        assertNotNull(outputDto, "Output DTO should not be null.");
        assertEquals(52, outputDto.getPhases().size(), "There should be 52 phases for a full cycle year.");

        // Print all phases with their dates and types
        System.out.println("All Phases:");
        outputDto.getPhases().forEach(phase ->
                System.out.println("Phase: " + phase.getStartDate() + " to " + phase.getEndDate() + ": " + phase.getPhaseName())
        );

        // Define expected phases to validate
        Object[][] expectedPhases = {
                {LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5), "Menstruation"},
                {LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 13), "Follicular"},
                {LocalDate.of(2024, 1, 14), LocalDate.of(2024, 1, 15), "Ovulation"},
                {LocalDate.of(2024, 1, 16), LocalDate.of(2024, 1, 28), "Luteal"},
                {LocalDate.of(2024, 1, 29), LocalDate.of(2024, 2, 2), "Menstruation"},
                // Add more expected phases as needed
        };

        // Validate each phase in the output
        for (int i = 0; i < expectedPhases.length; i++) {
            LocalDate expectedStartDate = (LocalDate) expectedPhases[i][0];
            LocalDate expectedEndDate = (LocalDate) expectedPhases[i][1];
            String expectedPhaseName = (String) expectedPhases[i][2];

            PhaseDto actualPhase = outputDto.getPhases().get(i);

            assertEquals(expectedStartDate, actualPhase.getStartDate(), "Start date of phase " + (i + 1) + " should match.");
            assertEquals(expectedEndDate, actualPhase.getEndDate(), "End date of phase " + (i + 1) + " should match.");
            assertEquals(expectedPhaseName, actualPhase.getPhaseName(), "Phase name of phase " + (i + 1) + " should match.");
        }
    }

    @Test
    void testGetCycleNotFound() {
        when(cycleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.getCycle(1L), "Should throw ResourceNotFoundException if cycle is not found.");
    }

    @Test
    void testUpdateCycle() {
        Cycle cycle = new Cycle();
        cycle.setStartDate(LocalDate.of(2024, 1, 1));
        when(cycleRepository.findById(1L)).thenReturn(Optional.of(cycle));

        CycleInputDto inputDto = new CycleInputDto();
        inputDto.setStartDate(LocalDate.of(2024, 2, 1));

        cycleService.updateCycle(1L, inputDto);

        verify(cycleRepository, times(1)).save(cycle);
        assertEquals(LocalDate.of(2024, 2, 1), cycle.getStartDate(), "Cycle start date should be updated.");

        // Check the new phases after the update
        List<PhaseDto> updatedPhases = cycleService.createPhases(cycle.getStartDate());

        // Print all phases with their dates and types for verification
        System.out.println("Updated Phases:");
        updatedPhases.forEach(phase ->
                System.out.println("Phase: " + phase.getStartDate() + " to " + phase.getEndDate() + ": " + phase.getPhaseName())
        );

        // Define expected phases to validate
        Object[][] expectedPhases = {
                {LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 5), "Menstruation"},
                {LocalDate.of(2024, 2, 6), LocalDate.of(2024, 2, 13), "Follicular"},
                {LocalDate.of(2024, 2, 14), LocalDate.of(2024, 2, 15), "Ovulation"},
                {LocalDate.of(2024, 2, 16), LocalDate.of(2024, 2, 28), "Luteal"},
                {LocalDate.of(2024, 2, 29), LocalDate.of(2024, 3, 4), "Menstruation"},
                // Add more expected phases as needed
        };

        // Validate each phase in the output
        for (int i = 0; i < expectedPhases.length; i++) {
            LocalDate expectedStartDate = (LocalDate) expectedPhases[i][0];
            LocalDate expectedEndDate = (LocalDate) expectedPhases[i][1];
            String expectedPhaseName = (String) expectedPhases[i][2];

            PhaseDto actualPhase = updatedPhases.get(i);

            assertEquals(expectedStartDate, actualPhase.getStartDate(), "Start date of updated phase " + (i + 1) + " should match.");
            assertEquals(expectedEndDate, actualPhase.getEndDate(), "End date of updated phase " + (i + 1) + " should match.");
            assertEquals(expectedPhaseName, actualPhase.getPhaseName(), "Phase name of updated phase " + (i + 1) + " should match.");
        }
    }


    @Test
    void testUpdateCycleNotFound() {
        when(cycleRepository.findById(1L)).thenReturn(Optional.empty());

        CycleInputDto inputDto = new CycleInputDto();
        inputDto.setStartDate(LocalDate.of(2024, 2, 1));

        assertThrows(ResourceNotFoundException.class, () -> cycleService.updateCycle(1L, inputDto), "Should throw ResourceNotFoundException if cycle is not found.");
    }
}


