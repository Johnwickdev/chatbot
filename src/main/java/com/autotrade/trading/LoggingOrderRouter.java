package com.autotrade.trading;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic {@link OrderRouter} implementation that simply logs the order
 * details and returns a randomly generated UUID for each order.
 */
public class LoggingOrderRouter implements OrderRouter {

    private static final Logger log = LoggerFactory.getLogger(LoggingOrderRouter.class);

    @Override
    public String placeOrder(OrderIntent intent) {
        String orderId = UUID.randomUUID().toString();
        log.info("Placing order {}: instrument={}, side={}, type={}, qty={}, price={}, mode={}",
                orderId,
                intent.getInstrumentKey(),
                intent.getSide(),
                intent.getOrderType(),
                intent.getQuantity(),
                intent.getPrice(),
                intent.getMode());
        return orderId;
    }

    @Override
    public boolean cancelOrder(String orderId) {
        log.info("Cancel order {}", orderId);
        return true;
    }
}

