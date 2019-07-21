package com.syniverse.wdm.interview.armedforces.view;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.syniverse.wdm.interview.armedforces.dto.Army;
import com.syniverse.wdm.interview.armedforces.dto.ArmyType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArmyDetailsView {

  private Long id;
  private String name;
  private ArmyType type;
  private List<UnitDetailsView> units;

  public static ArmyDetailsView fromArmy(final Army army) {
	  
    return ArmyDetailsView.builder().id(army.getId()).name(army.getName()).type(army.getType()).units( army.getUnits().stream().filter(Objects::nonNull).map(UnitDetailsView::fromUnit).collect(Collectors.toList())).build();
  }
}
