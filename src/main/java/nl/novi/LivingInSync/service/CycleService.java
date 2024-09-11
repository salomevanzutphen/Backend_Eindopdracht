package nl.novi.LivingInSync.service;

import nl.novi.LivingInSync.dto.PhaseDto;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.exception.ResourceNotFoundException;
import nl.novi.LivingInSync.model.*;
import nl.novi.LivingInSync.repository.CycleRepository;
import nl.novi.LivingInSync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CycleService {

    private final CycleRepository cycleRepository;
    private final UserRepository userRepository;

    @Autowired
    public CycleService(CycleRepository cycleRepository, UserRepository userRepository) {
        this.cycleRepository = cycleRepository;
        this.userRepository = userRepository;
    }

    public CycleOutputDto createOrUpdateCycle(CycleInputDto cycleInputDto, UserDetails userDetails) {
        User user = findUserByUsername(userDetails.getUsername());
        Cycle cycle = cycleRepository.findByCycleUser(user).orElseGet(() -> createNewCycle(cycleInputDto, user));
        updateCycleStartDate(cycle, cycleInputDto.getStartDate());
        return saveAndMapToDto(cycle);
    }

    public CycleOutputDto getUserCycle(UserDetails userDetails) {
        User user = findUserByUsername(userDetails.getUsername());
        Cycle cycle = findCycleByUser(user);
        return mapToOutputDto(cycle, createPhases(cycle.getStartDate()));
    }

    public CycleOutputDto updateCycleForUser(CycleInputDto cycleInputDto, UserDetails userDetails) {
        User user = findUserByUsername(userDetails.getUsername());
        Cycle cycle = findCycleByUser(user);
        updateCycleStartDate(cycle, cycleInputDto.getStartDate());
        return saveAndMapToDto(cycle);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private Cycle findCycleByUser(User user) {
        return cycleRepository.findByCycleUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found for the user"));
    }

    private Cycle createNewCycle(CycleInputDto cycleInputDto, User user) {
        Cycle cycle = mapToEntity(cycleInputDto);
        cycle.setCycleUser(user);
        return cycle;
    }

    private void updateCycleStartDate(Cycle cycle, LocalDate startDate) {
        cycle.setStartDate(startDate);
    }

    private CycleOutputDto saveAndMapToDto(Cycle cycle) {
        cycle = cycleRepository.save(cycle);
        return mapToOutputDto(cycle, createPhases(cycle.getStartDate()));
    }

    private Cycle mapToEntity(CycleInputDto cycleInputDto) {
        Cycle cycle = new Cycle();
        cycle.setStartDate(cycleInputDto.getStartDate());
        return cycle;
    }

    private CycleOutputDto mapToOutputDto(Cycle cycle, List<PhaseDto> phases) {
        CycleOutputDto outputDto = new CycleOutputDto();
        outputDto.setId(cycle.getId());
        outputDto.setPhases(phases);
        return outputDto;
    }

    public List<PhaseDto> createPhases(LocalDate startDate) {
        List<PhaseDto> phases = new ArrayList<>();
        LocalDate currentStartDate = startDate;

        for (int i = 0; i < 13; i++) {
            phases.add(new Menstruation(currentStartDate));
            currentStartDate = phases.get(phases.size() - 1).getEndDate().plusDays(1);

            phases.add(new Follicular(currentStartDate));
            currentStartDate = phases.get(phases.size() - 1).getEndDate().plusDays(1);

            phases.add(new Ovulation(currentStartDate));
            currentStartDate = phases.get(phases.size() - 1).getEndDate().plusDays(1);

            phases.add(new Luteal(currentStartDate));
            currentStartDate = phases.get(phases.size() - 1).getEndDate().plusDays(1);
        }

        return phases;
    }
}
