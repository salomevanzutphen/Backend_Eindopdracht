package nl.novi.LivingInSync.dto;

import java.time.LocalDate;

public class PhaseDto {
    private String phaseName;
    private LocalDate startDate;
    private LocalDate endDate;

    public PhaseDto(String phaseName, LocalDate startDate, LocalDate endDate) {
        this.phaseName = phaseName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PhaseDto() {

    }

    public String getPhaseName() {
        return phaseName;
    }

    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
