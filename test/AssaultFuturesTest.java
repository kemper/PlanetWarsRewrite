import static org.junit.Assert.*;

import org.junit.Test;


public class AssaultFuturesTest {
	int x, y, growthRate, ships, owner, id, enemy_id1, enemy_id2, my_id;

	@Test
	public void should_calculate_risk_value_with_growth() {
		Planet mine = new Planet(id = 7, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 0, y = 0);
		Planet mine2 = new Planet(id = 8, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 1, y = 0);
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 100, growthRate = 5, x = 2, y = 0);
		Planet enemy2 = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 20, growthRate = 5, x = 3, y = 0);
		Planet neutral = new Planet(id = 9, owner = PlanetWars.NEUTRAL_ID, ships = 20, growthRate = 5, x = 0, y = 0);
		Reservations reservations = new Reservations();
		Universe universe = new Universe(new Planets(neutral, enemy, enemy2, mine, mine2), new Fleets(), 1, reservations);
		AssaultFutures futures = new AssaultFutures(universe);
		assertEquals(10, futures.get(mine, 3).numShips);
	}

	@Test
	public void should_calculate_risk_value_considering_fleets_coming_to_planet_while_ignoring_ones_that_are_not() {
		Planet other = new Planet(id = 3, owner = PlanetWars.NEUTRAL_ID, ships = 10, growthRate = 5, x = 3, y = 0);
		Planet enemy = new Planet(enemy_id1 = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 1, y = 0);
		Planet mine = new Planet(id = 7, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 0, y = 0);
		Fleet myFleet = new Fleet(PlanetWars.PLAYER_ID, 10, id, other.id, 10, 1);
		Fleet enemyFleet = new Fleet(PlanetWars.ENEMY_ID, 20, enemy_id1, id, 10, 2);
		Reservations reservations = new Reservations();
		Universe universe = new Universe(new Planets(enemy, mine, other), new Fleets(myFleet, enemyFleet), 1, reservations);
		AssaultFutures futures = new AssaultFutures(universe);
		assertEquals(75, futures.get(mine, 2).numShips);
	}
	
	@Test
	public void should_calculate_risk_value_ignores_fleets_that_are_not_coming_to_that_planet() {
		Planet enemy = new Planet(enemy_id1 = 5, owner = PlanetWars.ENEMY_ID, ships = 20, growthRate = 5, x = 1, y = 0);
		Planet mine = new Planet(my_id = 7, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 0, y = 0);
		Planet tooFarAway = new Planet(id = 8, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 100, y = 100);
		Fleet myFleet = new Fleet(PlanetWars.PLAYER_ID, 1, enemy_id1, id, 10, 1);
		Fleet enemyFleet = new Fleet(PlanetWars.ENEMY_ID, 2, my_id, id, 10, 2);
		Reservations reservations = new Reservations();
		Universe universe = new Universe(new Planets(enemy, mine, tooFarAway), new Fleets(myFleet, enemyFleet), 1, reservations);
		AssaultFutures futures = new AssaultFutures(universe);
		assertEquals(85, futures.get(mine, 3).numShips);
	}
	
	@Test
	public void should_calculate_risk_value_ignores_fleets_and_planets_that_are_outside_of_turn_limit() {
		Planet enemy = new Planet(enemy_id1 = 5, owner = PlanetWars.ENEMY_ID, ships = 20, growthRate = 5, x = 10, y = 0);
		Planet mine = new Planet(id = 7, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 0, y = 0);
		Fleet enemyFleet = new Fleet(PlanetWars.ENEMY_ID, 2, 0, id, 10, 10);
		Reservations reservations = new Reservations();
		Universe universe = new Universe(new Planets(enemy, mine), new Fleets(enemyFleet), 1, reservations);
		AssaultFutures futures = new AssaultFutures(universe);
		assertEquals(120, futures.get(mine, 4).numShips);
	}
	
	@Test
	public void should_calculate_risk_value_considering_fleets_that_can_go_through_other_planets() {
		Planet enemy = new Planet(enemy_id1 = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 1, y = 0);
		Planet enemy2 = new Planet(enemy_id2 = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 2, y = 0);
		Planet mine = new Planet(id = 7, owner = PlanetWars.PLAYER_ID, ships = 100, growthRate = 5, x = 0, y = 0);
		Fleet enemyFleet = new Fleet(PlanetWars.ENEMY_ID, 20, enemy_id2, enemy_id1, 1, 1);
		Reservations reservations = new Reservations();
		Universe universe = new Universe(new Planets(enemy, enemy2, mine), new Fleets(enemyFleet), 1, reservations);
		AssaultFutures futures = new AssaultFutures(universe);
		assertEquals(65, futures.get(mine).numShips);
	}
	
}
