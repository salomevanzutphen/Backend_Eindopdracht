package nl.novi.LivingInSync.model;

import nl.novi.LivingInSync.dto.PhaseDto;

import java.time.LocalDate;

public class Menstruation extends PhaseDto {
    private static final int DURATION = 5;

    public Menstruation(LocalDate startDate) {
        super("Menstruation", startDate, startDate.plusDays(DURATION - 1));
    }

    public void calculatePhase(LocalDate startDate) {
        setStartDate(startDate);
        setEndDate(startDate.plusDays(DURATION - 1));
    }
}

