package io.trading.platform.provider.execution.alpaca;

import io.trading.platform.domain.enums.OrderStatus;
import io.trading.platform.domain.trading.OrderFilled;
import io.trading.platform.domain.trading.OrderUpdated;
import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdate;
import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdateEvent;
import net.jacobpeterson.alpaca.model.websocket.updates.model.tradeupdate.TradeUpdateMessage;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class AlpacaTradeUpdateNormalizer {

	private static final String PROVIDER = "ALPACA";

	public Optional<OrderUpdated> normalizeOrderUpdate(TradeUpdateMessage message) {
		TradeUpdate update = getTradeUpdate(message);
		if (update == null) {
			return Optional.empty();
		}

		Order order = update.getOrder();
		if (order == null || !StringUtils.hasText(order.getClientOrderId())) {
			return Optional.empty();
		}

		OrderStatus status = AlpacaOrderMapper.toDomainOrderStatus(order.getStatus());
		OffsetDateTime updatedAt = order.getUpdatedAt() != null ? order.getUpdatedAt() : update.getTimestamp();

		return Optional.of(new OrderUpdated(
				order.getClientOrderId(),
				status,
				updatedAt,
				PROVIDER
		));
	}

	public Optional<OrderFilled> normalizeOrderFill(TradeUpdateMessage message) {
		TradeUpdate update = getTradeUpdate(message);
		if (update == null || update.getEvent() == null) {
			return Optional.empty();
		}

		if (update.getEvent() != TradeUpdateEvent.FILL && update.getEvent() != TradeUpdateEvent.PARTIAL_FILL) {
			return Optional.empty();
		}

		Order order = update.getOrder();
		if (order == null || !StringUtils.hasText(order.getClientOrderId())) {
			return Optional.empty();
		}

		return Optional.of(new OrderFilled(
				order.getClientOrderId(),
				update.getExecutionId(),
				parseDecimal(update.getPrice()),
				parseDecimal(update.getQuantity()),
				update.getTimestamp(),
				PROVIDER
		));
	}

	private static TradeUpdate getTradeUpdate(TradeUpdateMessage message) {
		return message == null ? null : message.getData();
	}

	private static BigDecimal parseDecimal(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
