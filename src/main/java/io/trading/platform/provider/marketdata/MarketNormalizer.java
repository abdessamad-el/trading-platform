package io.trading.platform.provider.marketdata;

public interface MarketNormalizer<T, R> {

	R normalize(T message);
}
