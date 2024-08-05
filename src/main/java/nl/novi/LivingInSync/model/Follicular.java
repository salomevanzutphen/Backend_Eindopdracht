package nl.novi.LivingInSync.model;

import nl.novi.LivingInSync.dto.PhaseDto;

import java.time.LocalDate;

public class Follicular extends PhaseDto {
    private static final int DURATION = 8;

    public Follicular(LocalDate startDate) {
        super("Follicular", startDate, startDate.plusDays(DURATION - 1));
    }

    public void calculatePhase(LocalDate startDate) {
        setStartDate(startDate);
        setEndDate(startDate.plusDays(DURATION - 1));
    }
}
