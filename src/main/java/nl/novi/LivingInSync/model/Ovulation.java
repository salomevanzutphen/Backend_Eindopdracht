package nl.novi.LivingInSync.model;

import nl.novi.LivingInSync.dto.PhaseDto;

import java.time.LocalDate;

public class Ovulation extends PhaseDto {
    private static final int DURATION = 2;

    public Ovulation(LocalDate startDate) {
        super("Ovulation", startDate, startDate.plusDays(DURATION - 1));
    }

    public void calculatePhase(LocalDate startDate) {
        setStartDate(startDate);
        setEndDate(startDate.plusDays(DURATION - 1));
    }
}
