package io.trading.platform.provider.marketdata.alpaca;

import io.trading.platform.domain.market.StockMinuteBarUpdated;
import io.trading.platform.kafka.producer.StockMinuteBarEventProducer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import net.jacobpeterson.alpaca.websocket.marketdata.streams.stock.StockMarketDataListener;
import net.jacobpeterson.alpaca.websocket.marketdata.streams.stock.StockMarketDataListenerAdapter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AlpacaMarketDataListener {

	private final AlpacaProperties properties;
	private final AlpacaStockMinuteBarNormalizer normalizer;
	private final StockMinuteBarEventProducer barEventProducer;
	private AlpacaAPI alpacaAPI;

	public AlpacaMarketDataListener(AlpacaProperties properties,
			AlpacaStockMinuteBarNormalizer normalizer,
			StockMinuteBarEventProducer barEventProducer) {
		this.properties = properties;
		this.normalizer = normalizer;
		this.barEventProducer = barEventProducer;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void start() {
		if (!properties.hasCredentials()) {
			log.warn("Alpaca API credentials are not configured; skipping stock bar stream.");
			return;
		}

		List<String> symbols = properties.getMarketData().getSymbols();
		if (symbols == null || symbols.isEmpty()) {
			log.warn("No Alpaca stock symbols configured; skipping stock bar stream.");
			return;
		}

		alpacaAPI = new AlpacaAPI(
				properties.getApiKey(),
				properties.getApiSecret(),
				properties.getEndpointType(),
				properties.getMarketData().getFeed()
		);

		alpacaAPI.stockMarketDataStream().setListener(stockListener);
		alpacaAPI.stockMarketDataStream().connect();
		if (!alpacaAPI.stockMarketDataStream().waitForAuthorization(5, TimeUnit.SECONDS)) {
			log.error("Alpaca market data authorization failed.");
			alpacaAPI.stockMarketDataStream().disconnect();
			return;
		}

		alpacaAPI.stockMarketDataStream().setMinuteBarSubscriptions(new HashSet<>(symbols));
		log.info("Subscribed to Alpaca stock minute bars: {}", symbols);
	}

	private final StockMarketDataListener stockListener = new StockMarketDataListenerAdapter() {

		@Override
		public void onMinuteBar(StockBarMessage bar) {
			handleMinuteBar(bar);
		}
	};

	private void handleMinuteBar(StockBarMessage bar) {
		try {
			StockMinuteBarUpdated normalized = normalizer.normalize(bar);
			barEventProducer.publish(normalized);
		} catch (Exception ex) {
			log.warn("Failed to publish stock minute bar for {}.", bar.getSymbol(), ex);
		}
	}

	@PreDestroy
	public void stop() {
		if (alpacaAPI != null) {
			alpacaAPI.stockMarketDataStream().disconnect();
		}
	}
}
