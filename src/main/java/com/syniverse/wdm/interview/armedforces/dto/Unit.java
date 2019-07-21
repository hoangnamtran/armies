package com.syniverse.wdm.interview.armedforces.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Unit {

  private Long id;
  private UnitType type;
  @Min(1)
  @Max(100)
  private Long combatPower;
}
