package com.example.serviceTest;

import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.*;
import nl.novi.LivingInSync.repository.CycleRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import nl.novi.LivingInSync.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CycleServiceTest {

    @Mock
    private CycleRepository cycleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CycleService cycleService;

    @Mock
    private UserDetails userDetails;

    private User user;
    private CycleInputDto cycleInputDto;
    private Cycle cycle;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");

        cycleInputDto = new CycleInputDto();
        cycleInputDto.setStartDate(LocalDate.now());

        cycle = new Cycle();
        cycle.setStartDate(LocalDate.now());
        cycle.setCycleUser(user);

        when(userDetails.getUsername()).thenReturn("testUser");
    }

    @Test
    void testCreateOrUpdateCycle_NewCycle() {
        LocalDate expectedStartDate = LocalDate.of(2023, 1, 1);
        cycleInputDto.setStartDate(expectedStartDate);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.empty());
        when(cycleRepository.save(any(Cycle.class))).thenAnswer(invocation -> {
            Cycle savedCycle = invocation.getArgument(0);
            savedCycle.setId(1L); // Simulate ID assignment on save
            return savedCycle;
        });

        CycleOutputDto result = cycleService.createOrUpdateCycle(cycleInputDto, userDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(expectedStartDate, result.getPhases().get(0).getStartDate());
        verify(cycleRepository, times(1)).save(any(Cycle.class));
    }

    @Test
    void testCreateOrUpdateCycle_UpdateExistingCycle() {
        LocalDate originalStartDate = LocalDate.of(2023, 1, 1);
        LocalDate updatedStartDate = LocalDate.of(2023, 2, 1);

        // Set the original start date in the existing cycle
        cycle.setStartDate(originalStartDate);

        // Set the updated start date in the input DTO
        cycleInputDto.setStartDate(updatedStartDate);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.of(cycle));
        when(cycleRepository.save(any(Cycle.class))).thenAnswer(invocation -> {
            Cycle savedCycle = invocation.getArgument(0);
            savedCycle.setId(1L); // Simulate ID assignment on save
            return savedCycle;
        });

        CycleOutputDto result = cycleService.createOrUpdateCycle(cycleInputDto, userDetails);

        assertNotNull(result);
        assertEquals(1L, result.getId());  // Assuming ID is 1
        assertEquals(updatedStartDate, result.getPhases().get(0).getStartDate());
        verify(cycleRepository, times(1)).save(any(Cycle.class));
    }

    @Test
    void testCreateOrUpdateCycle_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.createOrUpdateCycle(cycleInputDto, userDetails));
    }

    @Test
    void testGetUserCycle_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.of(cycle));

        CycleOutputDto result = cycleService.getUserCycle(userDetails);

        assertNotNull(result);
        assertEquals(cycle.getId(), result.getId());
    }

    @Test
    void testGetUserCycle_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.getUserCycle(userDetails));
    }

    @Test
    void testGetUserCycle_CycleNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.getUserCycle(userDetails));
    }

    @Test
    void testUpdateCycleForUser_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.of(cycle));
        when(cycleRepository.save(any(Cycle.class))).thenReturn(cycle);

        CycleOutputDto result = cycleService.updateCycleForUser(cycleInputDto, userDetails);

        assertNotNull(result);
        assertEquals(cycle.getId(), result.getId());
        verify(cycleRepository, times(1)).save(any(Cycle.class));
    }

    @Test
    void testUpdateCycleForUser_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.updateCycleForUser(cycleInputDto, userDetails));
    }

    @Test
    void testUpdateCycleForUser_CycleNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cycleService.updateCycleForUser(cycleInputDto, userDetails));
    }
}
