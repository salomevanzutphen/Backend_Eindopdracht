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
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.empty());
        when(cycleRepository.save(any(Cycle.class))).thenReturn(cycle);

        CycleOutputDto result = cycleService.createOrUpdateCycle(cycleInputDto, userDetails);

        assertNotNull(result);
        assertEquals(cycle.getId(), result.getId());
        verify(cycleRepository, times(1)).save(any(Cycle.class));
    }

    @Test
    void testCreateOrUpdateCycle_UpdateExistingCycle() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(cycleRepository.findByCycleUser(any(User.class))).thenReturn(Optional.of(cycle));
        when(cycleRepository.save(any(Cycle.class))).thenReturn(cycle);

        CycleOutputDto result = cycleService.createOrUpdateCycle(cycleInputDto, userDetails);

        assertNotNull(result);
        assertEquals(cycle.getId(), result.getId());
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
