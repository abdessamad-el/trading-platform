package io.trading.platform.api;


import io.trading.platform.domain.trading.OrderRequest;
import io.trading.platform.persistence.entity.Order;
import io.trading.platform.provider.execution.alpaca.AlpacaExecutionProvider;
import jakarta.validation.Valid;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final AlpacaExecutionProvider executionProvider;
  public OrderController(AlpacaExecutionProvider executionProvider) {this.executionProvider = executionProvider;}

  @PostMapping
  public Order createOrder(@Valid @RequestBody OrderRequest orderRequest) throws ApiException {
    return executionProvider.placeOrder(orderRequest);

  }
}
