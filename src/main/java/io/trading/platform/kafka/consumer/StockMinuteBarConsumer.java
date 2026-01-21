package io.trading.platform.kafka.consumer;

import io.trading.platform.domain.market.StockMinuteBarUpdated;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StockMinuteBarConsumer {

	@KafkaListener(topics = "${trading.kafka.topics.market-minute-bars}", groupId = "market-minute-bars")
	public void onMinuteBar(StockMinuteBarUpdated event) {
		System.out.println(event);
	}
}
