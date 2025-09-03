package com.autotrade.trading;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.autotrade.service.ExpirySelectorService;
import com.autotrade.service.LiveFeedService;
import com.autotrade.service.NseInstrumentService;
import com.trader.backend.web.dto.Tick;

/**
 * Orchestrates the trading session by selecting instruments, subscribing for
 * market data and forwarding ticks to the {@link StrategyEngine}.
 */
@Component
public class SessionOrchestrator {

    private final ExpirySelectorService expirySelectorService;
    private final NseInstrumentService nseInstrumentService;
    private final LiveFeedService liveFeedService;
    private final StrategyEngine strategyEngine;
    private final RiskManager riskManager;

    @Autowired
    public SessionOrchestrator(ExpirySelectorService expirySelectorService,
                               NseInstrumentService nseInstrumentService,
                               LiveFeedService liveFeedService,
                               StrategyEngine strategyEngine,
                               RiskManager riskManager) {
        this.expirySelectorService = expirySelectorService;
        this.nseInstrumentService = nseInstrumentService;
        this.liveFeedService = liveFeedService;
        this.strategyEngine = strategyEngine;
        this.riskManager = riskManager;
    }

    @PostConstruct
    public void init() {
        expirySelectorService.ensureCurrentWeeklyExpiry();
        String futureKey = nseInstrumentService.getCurrentMonthFutureKey("NIFTY");
        strategyEngine.setInstrument(futureKey);
        liveFeedService.subscribe(futureKey);

        List<String> optionKeys = nseInstrumentService.filterStrikesAroundLtp(futureKey, 10);
        for (String key : optionKeys) {
            liveFeedService.subscribe(key);
        }
    }

    @EventListener
    public void handleTick(Tick tick) {
        strategyEngine.onTick(tick);
    }

    @Scheduled(cron = "0 1 0 * * *", zone = "Asia/Kolkata")
    public void resetDailyLimits() {
        riskManager.resetIfNewDay();
    }
}

