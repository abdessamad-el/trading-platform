package io.trading.platform.domain.trading;


import io.trading.platform.domain.enums.OrderSide;
import io.trading.platform.domain.enums.OrderType;
import io.trading.platform.domain.enums.TimeInForce;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
  @NotBlank
  private String symbol;

  @Positive
  @Digits(integer = 18, fraction = 8)
  private String qty;

  @Positive
  @Digits(integer = 18, fraction = 8)
  private String notional;

  @NotNull
  private OrderSide side;

  @NotNull
  private OrderType type;

  private TimeInForce timeInForce;

  @Positive
  @Digits(integer = 18, fraction = 8)
  private String limitPrice;

  @Positive
  @Digits(integer = 18, fraction = 8)
  private String stopPrice;

}
