import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlanetTest {
	int x, y, growthRate, ships, owner, id;

	@Test
	public void should_reduce_the_number_of_ships_when_ships_were_received() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.PLAYER_ID, 1, 0, id, 10, 1);
		assertEquals(9, planet.receive(enemy).numShips);
		assertTrue(planet.receive(enemy).isEnemy());
	}
	
	@Test
	public void should_change_ownership_and_recalculate_ships() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.PLAYER_ID, 15, 0, id, 10, 1);
		assertEquals(5, planet.receive(enemy).numShips);
		assertTrue(planet.receive(enemy).isMine());
	}
	
	@Test
	public void should_change_ownership_and_recalculate_ships_for_enemy() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 15, 0, id, 10, 1);
		assertEquals(5, planet.receive(enemy).numShips);
		assertTrue(planet.receive(enemy).isEnemy());
	}
	
	@Test
	public void should_change_add_to_planets_ships_when_the_owner_is_the_same() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.PLAYER_ID, 15, 0, id, 10, 1);
		assertEquals(25, planet.receive(enemy).numShips);
		assertTrue(planet.receive(enemy).isMine());
	}
}
