package io.trading.platform.provider.execution.alpaca;

import io.trading.platform.domain.enums.OrderType;
import io.trading.platform.domain.trading.OrderRequest;
import io.trading.platform.persistence.entity.Account;
import io.trading.platform.persistence.entity.Instrument;
import io.trading.platform.persistence.entity.Order;
import io.trading.platform.persistence.repository.AccountRepository;
import io.trading.platform.persistence.repository.InstrumentRepository;
import io.trading.platform.persistence.repository.OrderRepository;
import io.trading.platform.provider.marketdata.alpaca.AlpacaProperties;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.PostOrderRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AlpacaExecutionProvider {

  private final AlpacaProperties properties;
  private final AccountRepository accountRepository;
  private final OrderRepository orderRepository;
  private final InstrumentRepository instrumentRepository;
  private AlpacaAPI alpacaAPI;


  public AlpacaExecutionProvider(AlpacaProperties properties,
                                 AccountRepository accountRepository,
                                 OrderRepository orderRepository, InstrumentRepository instrumentRepository
  ) {this.properties = properties;
    this.accountRepository = accountRepository;
    this.orderRepository = orderRepository;
    this.instrumentRepository = instrumentRepository;
  }


  @EventListener(ApplicationReadyEvent.class)
  public void start() {
    if (!properties.hasCredentials()) {
      log.warn("Alpaca API credentials are not configured");
      return;
    }
    alpacaAPI = new AlpacaAPI(
        properties.getApiKey(),
        properties.getApiSecret(),
        properties.getEndpointType(),
        properties.getMarketData().getFeed()
    );
  }


  @Transactional
  public Order placeOrder(OrderRequest request) throws ApiException {
    if (alpacaAPI == null) {
      throw new IllegalStateException("Alpaca API is not configured");
    }
    PostOrderRequest postOrderRequest = new PostOrderRequest()
        .symbol(request.getSymbol())
        .qty(request.getQty())
        .side(AlpacaOrderMapper.toAlpacaOrderSide(request.getSide()))
        .type(AlpacaOrderMapper.toAlpacaOrderType(request.getType()))
        .timeInForce(AlpacaOrderMapper.toAlpacaTimeInForce(request.getTimeInForce()));
    if (request.getType() == OrderType.LIMIT) {
      postOrderRequest.limitPrice(request.getLimitPrice());
    }
    net.jacobpeterson.alpaca.openapi.trader.model.Order  orderFromAlpaca = alpacaAPI.trader().orders().postOrder(postOrderRequest);

    Order order = Order.builder()
        .providerOrderId(orderFromAlpaca.getClientOrderId())
        .qty(orderFromAlpaca.getQty())
        .side(AlpacaOrderMapper.toDomainOrderSide(orderFromAlpaca.getSide()))
        .type(AlpacaOrderMapper.toDomainOrderType(orderFromAlpaca.getType()))
        .limitPrice(orderFromAlpaca.getLimitPrice())
        .status(AlpacaOrderMapper.toDomainOrderStatus(orderFromAlpaca.getStatus()))
        .submittedAt(orderFromAlpaca.getSubmittedAt())
        .updatedAt(orderFromAlpaca.getUpdatedAt())
        .timeInForce(AlpacaOrderMapper.toDomainTimeInForce(orderFromAlpaca.getTimeInForce()))
        .build();

    order.setInstrument(getOrCreateInstrument(orderFromAlpaca));
    order.setAccount(getOrCreateAccount());
    return orderRepository.save(order);

  }

  private Instrument getOrCreateInstrument(net.jacobpeterson.alpaca.openapi.trader.model.Order orderFromAlpaca) {
    String symbol = orderFromAlpaca.getSymbol();
    Instrument instrument = instrumentRepository.findById(symbol).orElse(null);
    if (instrument == null) {
      instrument = Instrument.builder()
          .symbol(symbol)
          .assetClass(AlpacaOrderMapper.toDomainAssetClass(orderFromAlpaca.getAssetClass()))
          .active(true)
          .build();
      instrument = instrumentRepository.save(instrument);
    }
    return instrument;
  }


  private Account getOrCreateAccount() throws ApiException {
    net.jacobpeterson.alpaca.openapi.trader.model.Account accountFromAlpaca =
        alpacaAPI.trader().accounts().getAccount();
    Account account = accountRepository.findByProviderAccountId(accountFromAlpaca.getAccountNumber());
    if (account == null) {
      account = Account.builder()
          .providerAccountId(accountFromAlpaca.getAccountNumber())
          .environment(properties.getEndpointType().value())
          .currency(accountFromAlpaca.getCurrency())
          .createdDate(accountFromAlpaca.getCreatedAt())
          .provider("ALPACA")
          .build();

      account = accountRepository.save(account);
    }
    return account;
  }
}
