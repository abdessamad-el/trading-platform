package io.trading.platform.kafka.producer;

import io.trading.platform.domain.trading.OrderUpdated;
import io.trading.platform.kafka.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderUpdateEventProducer {

	private final KafkaTemplate<String, OrderUpdated> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	public OrderUpdateEventProducer(KafkaTemplate<String, OrderUpdated> kafkaTemplate,
			KafkaTopicsProperties topics) {
		this.kafkaTemplate = kafkaTemplate;
		this.topics = topics;
	}

	public void publish(OrderUpdated event) {
		kafkaTemplate.send(
				topics.getOrderUpdates(),
				event.providerOrderId(),
				event
		);
	}
}
