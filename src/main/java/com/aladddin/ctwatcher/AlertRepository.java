package com.aladddin.ctwatcher;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    boolean existsByCandidateAndWatched(String candidate, String watched);
}