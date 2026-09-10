package dev.mustafa.mini_marqetplace.scheduler;

import dev.mustafa.mini_marqetplace.model.entity.OrderItem;
import dev.mustafa.mini_marqetplace.model.entity.enums.OrderStatus;
import dev.mustafa.mini_marqetplace.repository.OrderItemRepository;
import dev.mustafa.mini_marqetplace.repository.OrderRepository;
import dev.mustafa.mini_marqetplace.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OrderExpirationScheduler {
    private static final Logger log = LoggerFactory.getLogger(OrderExpirationScheduler.class);
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderExpirationScheduler(OrderRepository orderRepository,
                                    OrderItemRepository orderItemRepository,
                                    ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void cancelExpiredOrders() {
        List<Integer> expiredOrderIds = orderRepository.findExpiredPendingOrders(15);

        if (expiredOrderIds.isEmpty()) {
            return;
        }

        log.info("found {} expired pending orders started to cancel and restore stock...", expiredOrderIds.size());

        for (Integer orderId : expiredOrderIds) {
            List<OrderItem> items = orderItemRepository.getOrderItems(orderId);
            for (OrderItem item : items) {
                productRepository.restoreProductStock(item.getProductId(), item.getQuantity());
            }

            orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELLED);
            log.info("Order ID {}  cancelled because of expiration", orderId);
        }
    }
}
