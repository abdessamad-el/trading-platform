package io.trading.platform.kafka.consumer;

import io.trading.platform.domain.trading.OrderFilled;
import io.trading.platform.persistence.entity.Fill;
import io.trading.platform.persistence.entity.Order;
import io.trading.platform.persistence.repository.FillRepository;
import io.trading.platform.persistence.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class OrderFilledConsumer {

	private final OrderRepository orderRepository;
	private final FillRepository fillRepository;

	public OrderFilledConsumer(OrderRepository orderRepository, FillRepository fillRepository) {
		this.orderRepository = orderRepository;
		this.fillRepository = fillRepository;
	}

	@KafkaListener(topics = "${trading.kafka.topics.fills}", groupId = "order-fills")
	@Transactional
	public void onOrderFilled(OrderFilled event) {
		Order order = orderRepository.findByProviderOrderId(event.providerOrderId());
		if (order == null) {
			log.warn("Fill received for unknown provider order id {}", event.providerOrderId());
			return;
		}

		if (event.executionId() != null) {
			Fill existing = fillRepository.findByProviderTradeId(event.executionId());
			if (existing != null) {
				return;
			}
		}

		Fill fill = Fill.builder()
				.order(order)
				.providerTradeId(event.executionId())
				.filledAt(event.filledAt())
				.filledQuantity(event.filledQuantity())
				.filledPrice(event.filledPrice())
				.build();

		fillRepository.save(fill);
	}
}
