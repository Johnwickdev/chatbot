package com.autotrade.trading;

/**
 * Abstraction for routing orders to a broker or simulator.
 */
public interface OrderRouter {

    /**
     * Places the given order intent.
     *
     * @param intent description of the order to place
     * @return unique order identifier, or {@code null} if the order was rejected
     */
    String placeOrder(OrderIntent intent);

    /**
     * Attempts to cancel an existing order.
     *
     * @param orderId identifier returned from {@link #placeOrder(OrderIntent)}
     * @return {@code true} if the cancel request was acknowledged
     */
    boolean cancelOrder(String orderId);
}

