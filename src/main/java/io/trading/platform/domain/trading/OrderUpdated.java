package io.trading.platform.domain.trading;

import io.trading.platform.domain.enums.OrderStatus;

import java.time.OffsetDateTime;

public record OrderUpdated(
		String providerOrderId,
		OrderStatus status,
		OffsetDateTime updatedAt,
		String provider
) {
}
