package com.aladddin.ctwatcher;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "alerts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate", "watched"}))
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
