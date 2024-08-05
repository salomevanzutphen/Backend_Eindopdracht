package nl.novi.LivingInSync.dto.input;

import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class CycleInputDto {
    @PastOrPresent
    private LocalDate startDate;


    private String cycleUserId;

    public CycleInputDto() {
    }

    public CycleInputDto(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getCycleUserId() {
        return cycleUserId;
    }

    public void setCycleUserId(String cycleUserId) {
        this.cycleUserId = cycleUserId;
    }

}