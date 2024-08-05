package nl.novi.LivingInSync.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Phases")
public class Phase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Cycle_id")
    private Cycle cycle;


    @Column(name = "Phase_name")
    private String phase;

    @Column(name = "Start_date")
    private LocalDate startDate;

    @Column(name = "End_date")
    private LocalDate endDate;

    @Column(name = "Duration")
    private int duration;

    // Default constructor
    public Phase() {
    }

    // Parameterized constructor
    public Phase(String phase, LocalDate startDate, LocalDate endDate, int duration) {
        this.phase = phase;
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
    }

    public Phase(String menstruation, LocalDate currentStartDate, LocalDate localDate, int i, Cycle cycle) {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cycle getCycle() {
        return cycle;
    }

    public void setCycle(Cycle cycle) {
        this.cycle = cycle;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}