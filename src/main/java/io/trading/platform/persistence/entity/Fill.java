package io.trading.platform.persistence.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fills")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fill {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID fillId;

  @ManyToOne
  @JoinColumn(name = "order_id")
  @JsonIgnore
  private Order order;

  private String providerTradeId;
  private BigDecimal fee;
  private String feeCurrency;
  private OffsetDateTime filledAt;
  private BigDecimal filledQuantity;
  private BigDecimal filledPrice;


}
