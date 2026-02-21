package io.trading.platform.kafka.consumer;

import io.trading.platform.domain.trading.OrderUpdated;
import io.trading.platform.persistence.entity.Order;
import io.trading.platform.persistence.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@Slf4j
public class OrderUpdateConsumer {

	private final OrderRepository orderRepository;

	public OrderUpdateConsumer(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@KafkaListener(topics = "${trading.kafka.topics.order-updates}", groupId = "order-updates")
	@Transactional
	public void onOrderUpdate(OrderUpdated event) {
		Order order = orderRepository.findByProviderOrderId(event.providerOrderId());
		if (order == null) {
			log.warn("Order update received for unknown provider order id {}", event.providerOrderId());
			return;
		}

		order.setStatus(event.status());
		OffsetDateTime updatedAt = event.updatedAt() != null ? event.updatedAt() : OffsetDateTime.now();
		order.setUpdatedAt(updatedAt);
		orderRepository.save(order);
	}
}
