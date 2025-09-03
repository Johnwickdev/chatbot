package com.autotrade.trading;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Maintains simple risk controls such as daily loss limits and position caps.
 */
public class RiskManager {

    private final double dailyLossCap;
    private final int maxOpenPositions;
    private final int maxQuantityPerOrder;

    private double cumulativePnl;
    private int openPositions;
    private LocalDate lastResetDate;

    public RiskManager(double dailyLossCap, int maxOpenPositions, int maxQuantityPerOrder) {
        this.dailyLossCap = dailyLossCap;
        this.maxOpenPositions = maxOpenPositions;
        this.maxQuantityPerOrder = maxQuantityPerOrder;
        this.cumulativePnl = 0.0;
        this.openPositions = 0;
        this.lastResetDate = LocalDate.now(ZoneId.of("Asia/Kolkata"));
    }

    /**
     * Checks whether the strategy can enter a new position with the specified
     * quantity given the configured limits and daily P&L.
     */
    public synchronized boolean canEnter(int quantity) {
        resetIfNewDay();
        if (quantity > maxQuantityPerOrder) {
            return false;
        }
        if (openPositions >= maxOpenPositions) {
            return false;
        }
        if (cumulativePnl <= -dailyLossCap) {
            return false;
        }
        return true;
    }

    /** Records that a new position has been entered. */
    public synchronized void recordEntry() {
        openPositions++;
    }

    /**
     * Records exit of a position along with realised P&L.
     *
     * @param pnl profit or loss realised on exit
     */
    public synchronized void recordExit(double pnl) {
        if (openPositions > 0) {
            openPositions--;
        }
        cumulativePnl += pnl;
    }

    /**
     * Resets daily counters if the day has changed (in Asia/Kolkata timezone).
     */
    public synchronized void resetIfNewDay() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        if (!today.equals(lastResetDate)) {
            cumulativePnl = 0.0;
            openPositions = 0;
            lastResetDate = today;
        }
    }
}

