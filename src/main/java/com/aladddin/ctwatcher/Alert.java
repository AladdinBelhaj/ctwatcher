package com.aladddin.ctwatcher;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String candidate;
    private String watched;
    private Instant firstSeen;

    protected Alert() {
    }

    public Alert(String candidate, String watched) {
        this.candidate = candidate;
        this.watched = watched;
        this.firstSeen = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCandidate() {
        return candidate;
    }

    public String getWatched() {
        return watched;
    }

    public Instant getFirstSeen() {
        return firstSeen;
    }
}
