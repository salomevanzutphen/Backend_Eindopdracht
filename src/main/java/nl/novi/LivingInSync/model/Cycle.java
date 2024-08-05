package nl.novi.LivingInSync.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Cycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    @OneToOne
    @JoinColumn(name = "cycle_user", referencedColumnName = "username", nullable = false)
    private User cycleUser;

    // Getters and setters

    public Cycle() {
    }

    public Cycle(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public User getCycleUser() {
        return cycleUser;
    }

    public void setCycleUser(User cycleUser) {
        this.cycleUser = cycleUser;
    }
}
