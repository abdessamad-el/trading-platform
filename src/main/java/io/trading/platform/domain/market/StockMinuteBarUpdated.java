package io.trading.platform.domain.market;

import java.time.OffsetDateTime;

public record StockMinuteBarUpdated(
		String symbol,
		Double open,
		Double high,
		Double low,
		Double close,
		OffsetDateTime timestamp,
		Long volume,
		Long tradeCount,
		Double vwap,
		String provider,
		String feed
) {
}
