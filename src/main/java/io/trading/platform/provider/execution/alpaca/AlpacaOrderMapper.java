package io.trading.platform.provider.execution.alpaca;

import io.trading.platform.domain.enums.AssetClass;
import io.trading.platform.domain.enums.OrderSide;
import io.trading.platform.domain.enums.OrderStatus;
import io.trading.platform.domain.enums.OrderType;
import io.trading.platform.domain.enums.TimeInForce;

public final class AlpacaOrderMapper {

  private AlpacaOrderMapper() {
  }

  public static net.jacobpeterson.alpaca.openapi.trader.model.OrderSide toAlpacaOrderSide(OrderSide side) {
    if (side == null) {
      throw new IllegalArgumentException("Order side is required");
    }
    return switch (side) {
      case BUY -> net.jacobpeterson.alpaca.openapi.trader.model.OrderSide.BUY;
      case SELL -> net.jacobpeterson.alpaca.openapi.trader.model.OrderSide.SELL;
    };
  }

  public static net.jacobpeterson.alpaca.openapi.trader.model.OrderType toAlpacaOrderType(OrderType type) {
    if (type == null) {
      throw new IllegalArgumentException("Order type is required");
    }
    return switch (type) {
      case MARKET -> net.jacobpeterson.alpaca.openapi.trader.model.OrderType.MARKET;
      case LIMIT -> net.jacobpeterson.alpaca.openapi.trader.model.OrderType.LIMIT;
    };
  }

  public static net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce toAlpacaTimeInForce(
      TimeInForce timeInForce) {
    if (timeInForce == null) {
      return net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.GTC;
    }
    return switch (timeInForce) {
      case DAY -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.DAY;
      case GTC -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.GTC;
      case OPG -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.OPG;
      case CLS -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.CLS;
      case IOC -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.IOC;
      case FOK -> net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce.FOK;
    };
  }

  public static OrderSide toDomainOrderSide(net.jacobpeterson.alpaca.openapi.trader.model.OrderSide side) {
    if (side == net.jacobpeterson.alpaca.openapi.trader.model.OrderSide.BUY) {
      return OrderSide.BUY;
    }
    return OrderSide.SELL;
  }

  public static OrderType toDomainOrderType(net.jacobpeterson.alpaca.openapi.trader.model.OrderType type) {
    if (type == net.jacobpeterson.alpaca.openapi.trader.model.OrderType.MARKET) {
      return OrderType.MARKET;
    }
    return OrderType.LIMIT;
  }

  public static TimeInForce toDomainTimeInForce(net.jacobpeterson.alpaca.openapi.trader.model.TimeInForce timeInForce) {
    if (timeInForce == null) {
      return TimeInForce.GTC;
    }
    return TimeInForce.valueOf(timeInForce.name());
  }

  public static OrderStatus toDomainOrderStatus(net.jacobpeterson.alpaca.openapi.trader.model.OrderStatus status) {
    if (status == null) {
      return OrderStatus.NEW;
    }
    return OrderStatus.valueOf(status.name());
  }

  public static AssetClass toDomainAssetClass(net.jacobpeterson.alpaca.openapi.trader.model.AssetClass assetClass) {
    if (assetClass == net.jacobpeterson.alpaca.openapi.trader.model.AssetClass.US_EQUITY) {
      return AssetClass.EQUITY;
    }
    if (assetClass == net.jacobpeterson.alpaca.openapi.trader.model.AssetClass.US_OPTION) {
      return AssetClass.OPTION;
    }
    return AssetClass.CRYPTO;
  }
}
