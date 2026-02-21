package io.trading.platform.kafka.producer;

import io.trading.platform.domain.trading.OrderFilled;
import io.trading.platform.kafka.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderFilledEventProducer {

	private final KafkaTemplate<String, OrderFilled> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	public OrderFilledEventProducer(KafkaTemplate<String, OrderFilled> kafkaTemplate,
			KafkaTopicsProperties topics) {
		this.kafkaTemplate = kafkaTemplate;
		this.topics = topics;
	}

	public void publish(OrderFilled event) {
		kafkaTemplate.send(
				topics.getFills(),
				event.providerOrderId(),
				event
		);
	}
}
