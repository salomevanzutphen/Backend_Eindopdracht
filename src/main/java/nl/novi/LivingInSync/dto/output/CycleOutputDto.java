package nl.novi.LivingInSync.dto.output;

import nl.novi.LivingInSync.dto.PhaseDto;

import java.util.List;

public class CycleOutputDto {
    private Long id;
    private List<PhaseDto> phases;

    public CycleOutputDto(Long id, List<PhaseDto> phases) {
        this.id = id;
        this.phases = phases;
    }

    public CycleOutputDto() {

    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PhaseDto> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDto> phases) {
        this.phases = phases;
    }
}