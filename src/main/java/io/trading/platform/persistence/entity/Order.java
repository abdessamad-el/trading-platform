package io.trading.platform.persistence.entity;


import io.trading.platform.domain.enums.OrderSide;
import io.trading.platform.domain.enums.OrderStatus;
import io.trading.platform.domain.enums.OrderType;
import io.trading.platform.domain.enums.TimeInForce;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID orderId;


  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;
  private String providerOrderId;

  @ManyToOne
  @JoinColumn(name = "symbol")
  private Instrument instrument;

  @Enumerated(EnumType.STRING)
  private OrderSide side;

  @Enumerated(EnumType.STRING)
  private OrderType type;

  private String qty;
  private String limitPrice;

  @Enumerated(EnumType.STRING)
  private TimeInForce timeInForce;


  @OneToMany(mappedBy = "order")
  private List<Fill> fills;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private OffsetDateTime submittedAt;
  private OffsetDateTime updatedAt;




}
