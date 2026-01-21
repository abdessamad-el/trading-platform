package io.trading.platform.provider.marketdata.alpaca;

import io.trading.platform.domain.market.StockMinuteBarUpdated;
import io.trading.platform.provider.marketdata.MarketNormalizer;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.bar.StockBarMessage;
import org.springframework.stereotype.Component;

@Component
public class AlpacaStockMinuteBarNormalizer implements MarketNormalizer<StockBarMessage, StockMinuteBarUpdated> {

	private static final String PROVIDER = "ALPACA";

	private final AlpacaProperties properties;

	public AlpacaStockMinuteBarNormalizer(AlpacaProperties properties) {
		this.properties = properties;
	}

	@Override
	public StockMinuteBarUpdated normalize(StockBarMessage bar) {
		return new StockMinuteBarUpdated(
				bar.getSymbol(),
				bar.getOpen(),
				bar.getHigh(),
				bar.getLow(),
				bar.getClose(),
				bar.getTimestamp(),
				bar.getVolume(),
				bar.getTradeCount(),
				bar.getVwap(),
				PROVIDER,
				properties.getMarketData().getFeed().name()
		);
	}
}
