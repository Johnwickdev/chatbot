package com.autotrade.trading;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for trading related beans.
 */
@Configuration
public class TradingConfig {

    @Bean
    public OrderRouter orderRouter() {
        return new LoggingOrderRouter();
    }

    @Bean
    public RiskManager riskManager() {
        // defaults: ₹5k daily loss cap, max 3 open positions, 1 lot per order
        return new RiskManager(5000.0, 3, 1);
    }
}

