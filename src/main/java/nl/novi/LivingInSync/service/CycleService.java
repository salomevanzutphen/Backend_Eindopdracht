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

@Service
public class CycleService {

    private final CycleRepository cycleRepository;
    private final UserRepository userRepository;

    @Autowired
    public CycleService(CycleRepository cycleRepository, UserRepository userRepository) {
        this.cycleRepository = cycleRepository;

        this.userRepository = userRepository;
    }

    public CycleOutputDto createCycle(CycleInputDto cycleInputDto, UserDetails userDetails) {
//
        Cycle cycle = mapToEntity(cycleInputDto);
        User u = userRepository.getReferenceById(userDetails.getUsername());
        cycle.setCycleUser(u);
        cycle = cycleRepository.save(cycle);
        List<PhaseDto> phases = createPhases(cycle.getStartDate());
        CycleOutputDto outputDto = new CycleOutputDto();
        outputDto.setId(cycle.getId());
        outputDto.setPhases(phases);
        return outputDto;
    }

    public CycleOutputDto getCycle(Long id) {
        Cycle cycle = cycleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));
        List<PhaseDto> phases = createPhases(cycle.getStartDate());
        CycleOutputDto outputDto = new CycleOutputDto();
        outputDto.setId(cycle.getId());
        outputDto.setPhases(phases);
        return outputDto;
    }

    public CycleOutputDto updateCycle(Long id, CycleInputDto cycleInputDto) {
        Cycle cycle = cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));
        cycle.setStartDate(cycleInputDto.getStartDate());
        cycleRepository.save(cycle);
        List<PhaseDto> phases = createPhases(cycle.getStartDate());
        CycleOutputDto outputDto = new CycleOutputDto();
        outputDto.setId(cycle.getId());
        outputDto.setPhases(phases);
        return outputDto;
    }


    private Cycle mapToEntity(CycleInputDto cycleInputDto) {
        Cycle cycle = new Cycle();
        cycle.setStartDate(cycleInputDto.getStartDate());
        return cycle;
    }

    private CycleOutputDto mapToOutputDto(Cycle cycle) {
        List<PhaseDto> phases = createPhases(cycle.getStartDate());
        CycleOutputDto outputDto = new CycleOutputDto();
        outputDto.setId(cycle.getId());
        outputDto.setPhases(phases);
        return outputDto;
    }


    public List<PhaseDto> createPhases(LocalDate startDate) {
        List<PhaseDto> phases = new ArrayList<>();
        LocalDate currentStartDate = startDate;

        for (int i = 0; i < 13; i++) {
            // Menstruation phase
            Menstruation menstruation = new Menstruation(currentStartDate);
            phases.add(menstruation);
            currentStartDate = menstruation.getEndDate().plusDays(1);

            // Follicular phase
            Follicular follicular = new Follicular(currentStartDate);
            phases.add(follicular);
            currentStartDate = follicular.getEndDate().plusDays(1);

            // Ovulation phase
            Ovulation ovulation = new Ovulation(currentStartDate);
            phases.add(ovulation);
            currentStartDate = ovulation.getEndDate().plusDays(1);

            // Luteal phase
            Luteal luteal = new Luteal(currentStartDate);
            phases.add(luteal);
            currentStartDate = luteal.getEndDate().plusDays(1);
        }

        return phases;
    }


    private PhaseDto mapToPhaseDto(Phase phase) {
        PhaseDto phaseDto = new PhaseDto();
        phaseDto.setPhaseName(phase.getPhase());
        phaseDto.setStartDate(phase.getStartDate());
        phaseDto.setEndDate(phase.getEndDate());
        return phaseDto;
    }



}


