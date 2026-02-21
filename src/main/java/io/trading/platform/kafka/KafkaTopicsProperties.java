package io.trading.platform.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "trading.kafka.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
	private String marketQuotes = "market.quotes.v1";
	private String marketMinuteBars = "market.bars.minute.v1";
	private String orderUpdates = "trading.order_updates.v1";
	private String fills = "trading.fills.v1";
}
