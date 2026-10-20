package com.aladddin.ctwatcher;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LookalikeDetectorTest {

    private final LookalikeDetector detector = new LookalikeDetector();

    @Test
    void digitSwapIsLookalike() {
        assertTrue(detector.isLookalike("paypa1.com", "paypal.com"));
    }

    @Test
    void missingLetterIsLookalike() {
        assertTrue(detector.isLookalike("paypl.com", "paypal.com"));
    }

    @Test
    void identicalDomainIsNotLookalike() {
        assertFalse(detector.isLookalike("paypal.com", "paypal.com"));
    }

    @Test
    void unrelatedDomainIsNotLookalike() {
        assertFalse(detector.isLookalike("google.com", "paypal.com"));
    }
}