import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class UniverseTest {
	int x, y, growthRate, ships, owner, id, enemy_id1, enemy_id2;

	@Test
	public void should_move_fleets_by_one_towards_target_planet() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Fleet fleet = new Fleet(PlanetWars.ENEMY_ID, 31, 0, id, 10, 2);
		Fleet fleet2 = new Fleet(PlanetWars.ENEMY_ID, 31, 0, id, 10, 4);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 30, growthRate = 5, x = 5, y = 5);
		Universe universe = new Universe(new Planets(enemy, mine), new Fleets(fleet, fleet2));
		Universe next = universe.next();
		assertEquals(1, next.fleets.first().turnsRemaining);
		assertEquals(3, next.fleets.second().turnsRemaining);
	}

	@Test
	public void should_grow_planets_by_one_except_neutrals() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Planet neutral = new Planet(id = 6, owner = PlanetWars.NEUTRAL_ID, ships = 20, growthRate = 5, x = 5, y = 5);
		Universe universe = new Universe(new Planets(enemy, mine, neutral), new Fleets());
		Universe next = universe.next();
		assertEquals(enemy.numShips + enemy.growthRate, next.planets.first().numShips);
		assertEquals(mine.numShips + mine.growthRate, next.planets.second().numShips);
		assertEquals(neutral.numShips, next.planets.third().numShips);
	}

	@Test
	public void should_remove_fleet_when_it_arrives_at_destination() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet fleet = new Fleet(PlanetWars.ENEMY_ID, 31, 0, id, 10, 1);
		Universe universe = new Universe(new Planets(enemy, mine), new Fleets(fleet));
		Universe next = universe.next();
		assertEquals(0, next.fleets.size());
	}
	
	@Test
	public void should_cancel_equal_fleets() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 2, y = 0);
		Planet neutral = new Planet(id = 7, owner = PlanetWars.NEUTRAL_ID, ships = 20, growthRate = 5, x = 1, y = 0);
		Fleet fleet = new Fleet(PlanetWars.ENEMY_ID, 30, 0, id, 10, 1);
		Fleet fleet2 = new Fleet(PlanetWars.PLAYER_ID, 30, 0, id, 10, 1);
		Universe universe = new Universe(new Planets(enemy, mine, neutral), new Fleets(fleet, fleet2));
		Universe next = universe.next();
		assertTrue(next.planets.find(id).isNeutral());
		assertEquals(20, next.planets.find(id).numShips);
	}
	
	@Test
	public void should_not_own_a_neutral_planet_when_sending_the_exact_number_of_ships() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 2, y = 0);
		Planet neutral = new Planet(id = 7, owner = PlanetWars.NEUTRAL_ID, ships = 20, growthRate = 5, x = 1, y = 0);
		Fleet fleet = new Fleet(PlanetWars.PLAYER_ID, 20, 0, id, 10, 1);
		Universe universe = new Universe(new Planets(enemy, mine, neutral), new Fleets(fleet));
		Universe next = universe.next();
		assertTrue(next.planets.find(id).isNeutral());
		assertEquals(0, next.planets.find(id).numShips);
	}

	@Test
	public void should_remove_ships_from_neutral_planets_and_turn_make_it_owned_by_attacker() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet neutral = new Planet(id = 6, owner = PlanetWars.NEUTRAL_ID, ships = 20, growthRate = 5, x = 5, y = 5);
		Fleet fleet = new Fleet(PlanetWars.ENEMY_ID, 31, 0, id, 10, 1);
		Universe universe = new Universe(new Planets(enemy, neutral), new Fleets(fleet));
		Universe next = universe.next();
		assertEquals(11, next.planets.second().numShips);
		assertTrue(next.planets.second().isEnemy());
	}

	@Test
	public void should_remove_ships_for_reservations_if_they_can_be_met() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Reservation reservation = new Reservation(new Attack(mine, enemy, 10), 1, 1);
		Reservations reservations = new Reservations(reservation);
		Universe universe = new Universe(new Planets(enemy, mine), new Fleets(), 1, reservations);
		Universe next = universe.next();
		assertEquals(5, next.planets.myPlanets().first().numShips);
	}

	@Test
	public void should_remove_ships_for_reservations_if_they_can_be_met_on_first_turn() {
		Planet enemy = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 30, growthRate = 5, x = 0, y = 0);
		Planet mine = new Planet(id = 6, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Reservation reservation = new Reservation(new Attack(mine, enemy, 10), 1, 1);
		Reservations reservations = new Reservations(reservation);
		Universe universe = new Universe(new Planets(enemy, mine), new Fleets(), 1, reservations);
		Universe next = universe.next();
		assertEquals(5, next.planets.myPlanets().first().numShips);
	}
}
