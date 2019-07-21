package com.syniverse.wdm.interview.armedforces.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import com.syniverse.wdm.interview.armedforces.dto.Army;
import com.syniverse.wdm.interview.armedforces.dto.ArmyType;
import com.syniverse.wdm.interview.armedforces.dto.Unit;
import com.syniverse.wdm.interview.armedforces.dto.UnitType;

@Profile("repo-collections")
@Repository
public class ArmedForcesRepositoryCollectionImpl implements ArmedForcesRepository {

  private final Map<Long, Army> armies = new ConcurrentHashMap<>();

  @PostConstruct
  protected void initializeData() {

    // @formatter:off
    this.armies.put(1L,  Army.builder().id(1L).name("North navy").type(ArmyType.NAVY)
        .units(Arrays.asList(
            Unit.builder().id(1L).combatPower(20L).type(UnitType.CORVETTE).build(),
            Unit.builder().id(2L).combatPower(80L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(3L).combatPower(30L).type(UnitType.CORVETTE).build()))
        .build());

    this.armies.put(2L, Army.builder().id(2L).name("South navy").type(ArmyType.NAVY)
        .units(new ArrayList<>(Arrays.asList(
            Unit.builder().id(1L).combatPower(25L).type(UnitType.CORVETTE).build(),
            Unit.builder().id(2L).combatPower(55L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(3L).combatPower(45L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(4L).combatPower(65L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(5L).combatPower(35L).type(UnitType.CORVETTE).build(),
            Unit.builder().id(6L).combatPower(45L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(7L).combatPower(55L).type(UnitType.CORVETTE).build(),
            Unit.builder().id(8L).combatPower(65L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(9L).combatPower(95L).type(UnitType.AIRCRAFT_CARRIER).build(),
            Unit.builder().id(10L).combatPower(25L).type(UnitType.CORVETTE).build())))
        .build());

    this.armies.put(3L, Army.builder().id(3L).name("Royal Air Force").type(ArmyType.AIR_FORCE)
        .units(new ArrayList<>(Arrays.asList(
            Unit.builder().id(1L).combatPower(25L).type(UnitType.FIGHTER_JET).build(),
            Unit.builder().id(2L).combatPower(55L).type(UnitType.BOMBER).build(),
            Unit.builder().id(3L).combatPower(45L).type(UnitType.FIGHTER_JET).build(),
            Unit.builder().id(4L).combatPower(65L).type(UnitType.BOMBER).build(),
            Unit.builder().id(5L).combatPower(35L).type(UnitType.FIGHTER_JET).build(),
            Unit.builder().id(6L).combatPower(45L).type(UnitType.FIGHTER_JET).build(),
            Unit.builder().id(7L).combatPower(55L).type(UnitType.FIGHTER_JET).build(),
            Unit.builder().id(8L).combatPower(65L).type(UnitType.BOMBER).build(),
            Unit.builder().id(9L).combatPower(95L).type(UnitType.BOMBER).build(),
            Unit.builder().id(10L).combatPower(25L).type(UnitType.FIGHTER_JET).build())))
        .build());

    this.armies.put(4L, Army.builder().id(4L).name("15th Army").type(ArmyType.INFANTRY)
        .units(new ArrayList<>(Arrays.asList(
            Unit.builder().id(1L).combatPower(2L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(2L).combatPower(5L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(3L).combatPower(4L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(4L).combatPower(6L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(5L).combatPower(3L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(7L).combatPower(5L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(8L).combatPower(6L).type(UnitType.PARATROOPER).build(),
            Unit.builder().id(9L).combatPower(9L).type(UnitType.PARATROOPER).build())))
        .build());
    // @formatter:on
  }

  @Override
  public Long createArmy(final Army army) {
    if (this.armies.size() < 50) {
      final Long armyId = getNextArmyId();
      army.setId(armyId);
      this.armies.put(armyId, army);
      return armyId;
    } else {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
          "Cannot add more armies. You already have way too many to manage, Sir!");
    }
  }

  @Override
  public List<Army> getArmies() {
    return new ArrayList<>(this.armies.values());
  }

  @Override
  public List<Army> getArmiesByType(final String type) {
    return getArmies().stream()
        .filter(army -> StringUtils.isNotBlank(type) && type.equals(army.getType().toString()))
        .collect(Collectors.toList());
  }

  @Override
  public Army getArmyById(final Long armyId) {
    return Optional.ofNullable(this.armies.get(armyId))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Hmmm. That army does not seem to exist, Sir!"));
  }

  @Override
  public Long recruitUnit(final Long armyId, final Unit unit) {
    Army army = getArmyById(armyId);
    if (army.getType() != unit.getType().getArmyType()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Hmmm. The unit type is not acceptable in that army");
    }

    if (army.getUnits().size() < 100) {
      final Long unitId = getNextUnitId(army);
      unit.setId(unitId);
      List<Unit> units = new ArrayList<>(army.getUnits());
      addToList(units, Stream.of(Unit.builder().id(unitId).combatPower(unit.getCombatPower())
          .type(unit.getType()).build()));
      army.setUnits(units);
      this.armies.replace(armyId, army);
      return unitId;
    } else {
      throw new UnsupportedOperationException("This API is not implemented yet!");
    }
  }

  @Override
  public List<Unit> getUnitsOfArmy(final Long armyId) {
    return getArmyById(armyId).getUnits();
  }

  @Override
  public Unit getUnitById(final Long armyId, final Long unitId) {
    return getArmyById(armyId).getUnits().stream().filter(unit -> unitId.equals(unit.getId()))
        .findAny().orElse(null);
  }

  @Override
  public Unit killUnit(final Long armyId, final Long unitId) {
    Army army = getArmyById(armyId);
    List<Unit> units = new ArrayList<>(army.getUnits());
    Unit unit = IntStream.range(0, units.size()).filter(i -> units.get(i).getId().equals(unitId))
        .boxed().findFirst().map(i -> units.remove(((int) i)))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Hmmm. That unit does not seem to exist, Sir!"));
    
    removeUnitFromArmy(army, units);

    return unit;
  }
  
  @Override
  public Unit killStrongestUnit(final Long armyId, final Long unitId) {
    Army army = getArmyById(armyId);
    List<Unit> units = new ArrayList<>(army.getUnits());
    
    Unit[] maxObj = new Unit[1];
    units.stream().max(Comparator.comparing( Unit::getCombatPower )).ifPresent(unit -> maxObj[0] = unit);
    
    Unit unit = IntStream.range(0, units.size()).filter(i -> units.get(i).getId().equals(unitId) && maxObj[0] != null && maxObj[0].equals(units.get(i)))
        .boxed().findFirst().map(i -> units.remove(((int) i)))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Hmmm. That unit does not seem to exist, Sir!"));
    removeUnitFromArmy(army, units);

    return unit;
  }
  
  @Override
  public List<Army> mergeArmies() {
    List<Army> list = getArmies();
    return list.stream()
        .filter((Army army) -> army instanceof Army)
        .collect(Collectors.toList());
  }
  
  private void removeUnitFromArmy(Army army, List<Unit> units) {
    Long armyId = army.getId();
    army.setUnits(units);
    
    if (units.isEmpty()) {
      this.armies.remove(armyId, army);
    } else {
      this.armies.replace(armyId, army);
    }
  }

  private Long getNextArmyId() {
    return (this.armies.keySet().isEmpty() ? 0L : Collections.max(this.armies.keySet())) + 1L;
  }

  private Long getNextUnitId(final Army army) {
    return (army.getUnits().isEmpty() ? 0L
        : Collections.max(army.getUnits().stream().map(Unit::getId).collect(Collectors.toList())))
        + 1L;
  }

  public static <T> void addToList(List<T> target, Stream<T> source) {
    target.addAll(source.collect(Collectors.toList()));
  }
}
