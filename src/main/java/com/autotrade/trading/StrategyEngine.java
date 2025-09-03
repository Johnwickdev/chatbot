package com.autotrade.trading;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trader.backend.web.dto.Tick;
import com.autotrade.service.QuantAnalysisService;
import com.autotrade.service.QuantAnalysisService.Signal;

/**
 * Basic strategy engine that reacts to tick data and submits paper orders
 * through an {@link OrderRouter} after consulting the {@link RiskManager}.
 */
@Component
public class StrategyEngine {

    private final QuantAnalysisService quantAnalysisService;
    private final OrderRouter orderRouter;
    private final RiskManager riskManager;

    private volatile String currentInstrument;
    private final AtomicBoolean inPosition = new AtomicBoolean(false);
    private final AtomicReference<OrderIntent.Side> positionSide = new AtomicReference<>();

    @Autowired
    public StrategyEngine(QuantAnalysisService quantAnalysisService,
                          OrderRouter orderRouter,
                          RiskManager riskManager) {
        this.quantAnalysisService = quantAnalysisService;
        this.orderRouter = orderRouter;
        this.riskManager = riskManager;
    }

    /** Handles incoming ticks. */
    public void onTick(Tick tick) {
        if (currentInstrument == null || !currentInstrument.equals(tick.getInstrumentKey())) {
            return;
        }
        Signal signal = quantAnalysisService.computeAnalysis(tick);
        switch (signal) {
            case ENTRY_LONG -> handleEntry(tick, OrderIntent.Side.BUY);
            case ENTRY_SHORT -> handleEntry(tick, OrderIntent.Side.SELL);
            case EXIT -> handleExit(tick);
            default -> {
            }
        }
    }

    private void handleEntry(Tick tick, OrderIntent.Side side) {
        if (!inPosition.get() && riskManager.canEnter(1)) {
            OrderIntent intent = new OrderIntent(
                    UUID.randomUUID().toString(),
                    tick.getInstrumentKey(),
                    side,
                    OrderIntent.OrderType.MARKET,
                    OrderIntent.Mode.PAPER,
                    1,
                    tick.getLtp());
            String orderId = orderRouter.placeOrder(intent);
            if (orderId != null) {
                inPosition.set(true);
                positionSide.set(side);
                riskManager.recordEntry();
            }
        }
    }

    private void handleExit(Tick tick) {
        if (inPosition.get()) {
            OrderIntent.Side entrySide = positionSide.get();
            OrderIntent.Side exitSide = entrySide == OrderIntent.Side.BUY ? OrderIntent.Side.SELL : OrderIntent.Side.BUY;
            OrderIntent intent = new OrderIntent(
                    UUID.randomUUID().toString(),
                    tick.getInstrumentKey(),
                    exitSide,
                    OrderIntent.OrderType.MARKET,
                    OrderIntent.Mode.PAPER,
                    1,
                    tick.getLtp());
            String orderId = orderRouter.placeOrder(intent);
            if (orderId != null) {
                inPosition.set(false);
                positionSide.set(null);
                riskManager.recordExit(0.0);
            }
        }
    }

    /**
     * Updates the instrument for which ticks will be processed. Any existing
     * position state is reset.
     */
    public void setInstrument(String instrumentKey) {
        this.currentInstrument = instrumentKey;
        inPosition.set(false);
        positionSide.set(null);
    }
}

