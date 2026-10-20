package com.aladddin.ctwatcher;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LookalikeDetector {

    private static final int MAX_DISTANCE = 2;

    public boolean isLookalike(String candidate, String watched) {
        int labels = watched.split("\\.").length;
        String tail = tail(candidate, labels);
        if (tail == null || tail.equals(watched)) {
            return false;
        }
        return levenshtein(tail, watched) <= MAX_DISTANCE;
    }

    private static String tail(String domain, int labels) {
        String[] parts = domain.split("\\.");
        if (parts.length < labels) {
            return null;
        }
        return String.join(".", Arrays.copyOfRange(parts, parts.length - labels, parts.length));
    }

    private static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}