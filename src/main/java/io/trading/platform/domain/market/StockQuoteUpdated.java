package io.trading.platform.domain.market;

import java.time.OffsetDateTime;
import java.util.Set;

public record StockQuoteUpdated(
		String symbol,
		String askExchange,
		Double askPrice,
		Integer askSize,
		String bidExchange,
		Double bidPrice,
		Integer bidSize,
		Set<String> conditions,
		OffsetDateTime timestamp,
		String tape,
		String provider,
		String feed
) {
}
