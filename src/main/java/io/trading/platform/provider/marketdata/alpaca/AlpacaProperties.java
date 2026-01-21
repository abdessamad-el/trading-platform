package io.trading.platform.provider.marketdata.alpaca;

import lombok.Getter;
import lombok.Setter;
import net.jacobpeterson.alpaca.model.util.apitype.MarketDataWebsocketSourceType;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "alpaca")
@Getter
@Setter
public class AlpacaProperties {

	private String apiKey;
	private String apiSecret;
	private String tradingBaseUrl;
	private String dataBaseUrl;
	private TraderAPIEndpointType endpointType = TraderAPIEndpointType.PAPER;
	private MarketData marketData = new MarketData();

	public boolean hasCredentials() {
		return StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret);
	}

	@Getter
	@Setter
	public static class MarketData {

		private MarketDataWebsocketSourceType feed = MarketDataWebsocketSourceType.IEX;
		private List<String> symbols = new ArrayList<>();
	}
}

