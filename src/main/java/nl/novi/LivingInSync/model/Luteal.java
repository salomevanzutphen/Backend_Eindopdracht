package nl.novi.LivingInSync.model;

import nl.novi.LivingInSync.dto.PhaseDto;

import java.time.LocalDate;

public class Luteal extends PhaseDto {
    private static final int DURATION = 13;

    public Luteal(LocalDate startDate) {
        super("Luteal", startDate, startDate.plusDays(DURATION - 1));
    }

    public void calculatePhase(LocalDate startDate) {
        setStartDate(startDate);
        setEndDate(startDate.plusDays(DURATION - 1));
    }
}

