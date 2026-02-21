package io.trading.platform.domain.trading;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderFilled(
		String providerOrderId,
		String executionId,
		BigDecimal filledPrice,
		BigDecimal filledQuantity,
		OffsetDateTime filledAt,
		String provider
) {
}
