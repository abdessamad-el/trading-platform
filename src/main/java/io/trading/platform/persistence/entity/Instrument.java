package io.trading.platform.persistence.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.jacobpeterson.alpaca.openapi.trader.model.AssetClass;

import java.util.List;

@Entity
@Table(name = "instruments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument {


  @Id
  private String symbol;

  @Enumerated(EnumType.STRING)
  private AssetClass assetClass;

  private String exchange;
  private boolean active;

  @OneToMany(mappedBy = "instrument")
  List<Order> orders;

}
