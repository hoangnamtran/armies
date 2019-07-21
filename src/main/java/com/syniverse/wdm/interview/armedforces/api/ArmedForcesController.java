// $Id: CompanyResource.java 6699 2018-04-18 14:58:06Z g801797 $
// $URL: https://am1p-gen-ias-vcs001.syniverse.com/svn-am/obf/obf-rest/branches/obf_dev_Victor/src/main/java/com/syniverse/obf/company/ui/CompanyResource.java $
package com.syniverse.wdm.interview.armedforces.api;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syniverse.wdm.interview.armedforces.dto.Unit;
import com.syniverse.wdm.interview.armedforces.repository.ArmedForcesRepository;
import com.syniverse.wdm.interview.armedforces.view.ArmyDetailsView;
import com.syniverse.wdm.interview.armedforces.view.ArmyInputView;
import com.syniverse.wdm.interview.armedforces.view.UnitDetailsView;
import com.syniverse.wdm.interview.armedforces.view.UnitInputView;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/armed-forces/v1/")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ArmedForcesController {

  private final ArmedForcesRepository armedForcesRepository;

  @ApiOperation(value = "Create an army", notes = "Returns the ID of the newly created army")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = Long.class)})
  @PostMapping("/armies")
  public Long createArmy(@RequestBody final ArmyInputView army) {
    return this.armedForcesRepository.createArmy(Optional.ofNullable(army).map(ArmyInputView::toArmy).orElse(null));
  }

  @ApiOperation(value = "List the summary of all the armies", notes = "Returns a list of all armies")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = ArmyDetailsView.class, responseContainer = "List")})
  @GetMapping("/armies")
  public List<ArmyDetailsView> getArmies() {
    return this.armedForcesRepository.getArmies().stream().map(ArmyDetailsView::fromArmy).collect(Collectors.toList());
  }
  
  @ApiOperation(value = "List armies of a given type", notes = "Returns a list of armies")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = ArmyDetailsView.class, responseContainer = "List")})
  @GetMapping("/armies/{type:[\\w]+}")
  public List<ArmyDetailsView> getArmiesByType(@PathVariable(name = "type") final String type) {
	  return this.armedForcesRepository.getArmiesByType(type).stream().map(ArmyDetailsView::fromArmy).collect(Collectors.toList());
  }
  
  @ApiOperation(value = "Fetch armed forces executive summary", notes = "Returns a army")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = ArmyDetailsView.class)})
  @GetMapping("/army/{armyId:[\\d]+}")
  public ArmyDetailsView getArmy(@PathVariable(name = "armyId") final Long armyId) {
    return ArmyDetailsView.fromArmy(this.armedForcesRepository.getArmyById(armyId));
  }

  @ApiOperation(value = "Recruit an unit to the army", notes = "Returns the ID of the newly recruited unit")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = Long.class)})
  @PostMapping("/armies/{armyId:[\\d]+}/units")
  public Long recruitUnit(@PathVariable(name = "armyId") final Long armyId, @Valid @RequestBody final UnitInputView unit) {
    return this.armedForcesRepository.recruitUnit(armyId, Optional.ofNullable(unit).map(UnitInputView::toUnit).orElse(null));
  }

  @ApiOperation(value = "Fetch all units of the army", notes = "Returns a list of all units of the army")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class, responseContainer = "List")})
  @GetMapping("/armies/{armyId:[\\d]+}/units")
  public List<UnitDetailsView> getUnitsOfArmy(@PathVariable(name = "armyId") final Long armyId) {
    return this.armedForcesRepository.getUnitsOfArmy(armyId).stream().map(UnitDetailsView::fromUnit).collect(Collectors.toList());
  }
  
  @ApiOperation(value = "Fetch all units of the army sorted by combat power descending", notes = "Returns a list of units of the army")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class, responseContainer = "List")})
  @GetMapping("/armies/{armyId:[\\d]+}/units-sorted-combat-power-desc")
  public List<UnitDetailsView> getUnitsOfArmySortedByCombatPowerDescending(@PathVariable(name = "armyId") final Long armyId) {
    return this.armedForcesRepository.getUnitsOfArmy(armyId).stream().sorted(Comparator.comparing(Unit::getCombatPower).reversed()).map(UnitDetailsView::fromUnit).collect(Collectors.toList());
  }
  
  @ApiOperation(value = "Fetch those units of the army which have combat power 50 or more", notes = "Returns a list of units of the army")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class, responseContainer = "List")})
  @GetMapping("/armies/{armyId:[\\d]+}/units-combat-power-50-more")
  public List<UnitDetailsView> getUnitsOfArmyCombatPower50AndMore(@PathVariable(name = "armyId") final Long armyId) {
    return this.armedForcesRepository.getUnitsOfArmy(armyId).stream().filter(unit -> unit.getCombatPower() >= 50).map(UnitDetailsView::fromUnit).collect(Collectors.toList());
  }
  
  @ApiOperation(value = "Fetch the unit details", notes = "Returns an unit")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class)})
  @GetMapping("/armies/{armyId:[\\d]+}/unit/{unitId:[\\d]+}")
  public UnitDetailsView getUnit(@PathVariable(name = "armyId") final Long armyId, @PathVariable(name = "unitId") final Long unitId) {
	  return UnitDetailsView.fromUnit(this.armedForcesRepository.getUnitById(armyId, unitId));
  }
  
  @ApiOperation(value = "The given unit killed/destroyed (removed from the army)", notes = "Returns the ID of the unit which is killed")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class)})
  @PostMapping("/armies/{armyId:[\\d]+}/killUnit/{unitId:[\\d]+}")
  public UnitDetailsView killUnit(@PathVariable(name = "armyId") final Long armyId, @PathVariable(name = "unitId") final Long unitId) {
	  return UnitDetailsView.fromUnit(this.armedForcesRepository.killUnit(armyId, unitId));
  }
  
  @ApiOperation(value = "The given unit killed/destroyed (removed from the army)", notes = "Returns the ID of the unit which is killed")
  @ApiResponses({@ApiResponse(code = 200, message = "Success", response = UnitDetailsView.class)})
  @PostMapping("/armies/{armyId:[\\d]+}/killStrongestUnit/{unitId:[\\d]+}")
  public UnitDetailsView killStrongestUnit(@PathVariable(name = "armyId") final Long armyId, @PathVariable(name = "unitId") final Long unitId) {
      return UnitDetailsView.fromUnit(this.armedForcesRepository.killStrongestUnit(armyId, unitId));
  }
}
