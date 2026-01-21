package io.trading.platform.provider.marketdata.alpaca;

import io.trading.platform.domain.market.StockQuoteUpdated;
import io.trading.platform.provider.marketdata.MarketNormalizer;
import net.jacobpeterson.alpaca.model.websocket.marketdata.streams.stock.model.quote.StockQuoteMessage;
import org.springframework.stereotype.Component;

@Component
public class AlpacaStockQuoteNormalizer implements MarketNormalizer<StockQuoteMessage, StockQuoteUpdated> {

	private static final String PROVIDER = "ALPACA";

	private final AlpacaProperties properties;

	public AlpacaStockQuoteNormalizer(AlpacaProperties properties) {
		this.properties = properties;
	}

	@Override
	public StockQuoteUpdated normalize(StockQuoteMessage quote) {
		return new StockQuoteUpdated(
				quote.getSymbol(),
				quote.getAskExchange(),
				quote.getAskPrice(),
				quote.getAskSize(),
				quote.getBidExchange(),
				quote.getBidPrice(),
				quote.getBidSize(),
				quote.getConditions(),
				quote.getTimestamp(),
				quote.getTape(),
				PROVIDER,
				properties.getMarketData().getFeed().name()
		);
	}
}
