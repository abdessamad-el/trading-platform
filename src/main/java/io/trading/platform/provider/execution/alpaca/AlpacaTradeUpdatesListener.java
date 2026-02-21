package io.trading.platform.provider.execution.alpaca;

import io.trading.platform.kafka.producer.OrderFilledEventProducer;
import io.trading.platform.kafka.producer.OrderUpdateEventProducer;
import io.trading.platform.provider.marketdata.alpaca.AlpacaProperties;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdateMessage;
import net.jacobpeterson.alpaca.websocket.updates.UpdatesListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AlpacaTradeUpdatesListener {

	private final AlpacaProperties properties;
	private final AlpacaTradeUpdateNormalizer normalizer;
	private final OrderUpdateEventProducer orderUpdateEventProducer;
	private final OrderFilledEventProducer orderFilledEventProducer;
	private AlpacaAPI alpacaAPI;

	public AlpacaTradeUpdatesListener(AlpacaProperties properties,
			AlpacaTradeUpdateNormalizer normalizer,
			OrderUpdateEventProducer orderUpdateEventProducer,
			OrderFilledEventProducer orderFilledEventProducer) {
		this.properties = properties;
		this.normalizer = normalizer;
		this.orderUpdateEventProducer = orderUpdateEventProducer;
		this.orderFilledEventProducer = orderFilledEventProducer;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void start() {
		if (!properties.hasCredentials()) {
			log.warn("Alpaca API credentials are not configured; skipping trade update stream.");
			return;
		}

		alpacaAPI = new AlpacaAPI(
				properties.getApiKey(),
				properties.getApiSecret(),
				properties.getEndpointType(),
				properties.getMarketData().getFeed()
		);

		alpacaAPI.updatesStream().setListener(updatesListener);
		alpacaAPI.updatesStream().connect();
		if (!alpacaAPI.updatesStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
			log.error("Alpaca trade updates authorization failed.");
			alpacaAPI.updatesStream().disconnect();
			return;
		}

		alpacaAPI.updatesStream().subscribeToTradeUpdates(true);
		log.info("Subscribed to Alpaca trade updates.");
	}

	private final UpdatesListener updatesListener = this::handleTradeUpdate;

	private void handleTradeUpdate(TradeUpdateMessage update) {
		try {
			normalizer.normalizeOrderUpdate(update).ifPresent(orderUpdateEventProducer::publish);
			normalizer.normalizeOrderFill(update).ifPresent(orderFilledEventProducer::publish);
		} catch (Exception ex) {
			log.warn("Failed to publish Alpaca trade update.", ex);
		}
	}

	@PreDestroy
	public void stop() {
		if (alpacaAPI != null) {
			alpacaAPI.updatesStream().disconnect();
		}
	}
}
