package io.trading.platform.kafka.producer;

import io.trading.platform.domain.market.StockMinuteBarUpdated;
import io.trading.platform.kafka.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockMinuteBarEventProducer {

	private final KafkaTemplate<String, StockMinuteBarUpdated> kafkaTemplate;
	private final KafkaTopicsProperties topics;

	public StockMinuteBarEventProducer(KafkaTemplate<String, StockMinuteBarUpdated> kafkaTemplate,
			KafkaTopicsProperties topics) {
		this.kafkaTemplate = kafkaTemplate;
		this.topics = topics;
	}

	public void publish(StockMinuteBarUpdated event) {
		kafkaTemplate.send(
				topics.getMarketMinuteBars(),
				event.symbol(),
				event
		);
	}
}
