import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class UniversesTest {
	int x, y, growthRate, ships, owner, id, otherId;

	/**
	 * sparableShips
	 */
	@Test
	public void should_return_the_amount_remaining_after_the_fleets_have_played_out_minus_one() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 15, 0, id, 10, 1);
		Fleet mine2 = new Fleet(PlanetWars.PLAYER_ID, 15, 0, id, 10, 1);
		Fleet enemy2 = new Fleet(PlanetWars.ENEMY_ID, 25, 0, id, 10, 3);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy, enemy2, mine2), 10, 1);
		assertEquals(29, universes.sparableShips(planet));
	}
	
	@Test
	public void should_not_consider_fleets_going_to_other_planets() {
		Planet someOtherPlanet = new Planet(otherId = 6, owner = PlanetWars.PLAYER_ID, ships = 20, growthRate = 5, x = 5, y = 5);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 10, 0, id, 10, 1);
		Fleet enemy2 = new Fleet(PlanetWars.ENEMY_ID, 21, 0, otherId, 10, 3);
		Universes universes = new Universes(new Planets(someOtherPlanet, planet), new Fleets(enemy, enemy2), 10, 1);
		assertEquals(4, universes.sparableShips(planet));
	}
	
	@Test
	public void should_return_the_amount_remaining_when_no_fleets_are_attacking() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Universes universes = new Universes(new Planets(planet), new Fleets(), 10, 1);
		assertEquals(39, universes.sparableShips(planet));
	}
	
	@Test
	public void should_return_zero_when_no_fleets_are_attacking_and_numships_is_one() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 0, growthRate = 5, x = 5, y = 5);
		Universes universes = new Universes(new Planets(planet), new Fleets(), 10, 1);
		assertEquals(0, universes.sparableShips(planet));
	}
	
	@Test
	public void should_not_return_more_than_the_current_number_of_ships() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet mine = new Fleet(PlanetWars.PLAYER_ID, 15, 0, id, 10, 2);
		Universes universes = new Universes(new Planets(planet), new Fleets(mine), 10, 1);
		assertEquals(39, universes.sparableShips(planet));
	}

	@Test
	public void should_return_zero_when_the_planet_will_be_lost() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 55, 0, id, 10, 1);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy), 10, 1);
		assertEquals(0, universes.sparableShips(planet));
	}
	
	@Test
	public void should_return_zero_when_the_planet_will_be_lost_exactly() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 45, 0, id, 10, 1);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy), 10, 1);
		assertEquals(0, universes.sparableShips(planet));
	}
	
	@Test
	public void should_return_zero_when_sending_ships_now_will_result_in_a_loss_even_if_retaken_later() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 30, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 40, 0, id, 10, 1);
		Fleet mine2 = new Fleet(PlanetWars.PLAYER_ID, 100, 0, id, 10, 10);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy, mine2), 10, 1);
		assertEquals(0, universes.sparableShips(planet));
	}

	@Test
	public void should_return_the_amount_remaining_after_considering_fleets_and_one_reservation() {
		Planet enemyPlanet = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Planet planet2 = new Planet(id = 7, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 15, enemyPlanet.id, planet.id, 10, 1);
		Fleet mine2 = new Fleet(PlanetWars.PLAYER_ID, 15, planet2.id, planet.id, 10, 1);
		Reservation reservation = new Reservation(new Attack(planet, enemyPlanet, 10), 0, 2);
		Reservations reservations = new Reservations(reservation);
		Fleet enemy2 = new Fleet(PlanetWars.ENEMY_ID, 25, enemyPlanet.id, planet.id, 10, 3);
		Universes universes = new Universes(new Planets(planet, planet2, enemyPlanet), new Fleets(enemy, enemy2, mine2), 10, 1, reservations);
		assertEquals(19, universes.sparableShips(planet));
	}
		
	@Test
	public void should_return_the_amount_remaining_after_considering_fleets_and_an_unrelated_reservation() {
		Planet enemyPlanet = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet enemyPlanet2 = new Planet(id = 7, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 25, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 10, enemyPlanet.id, planet.id, 10, 2);
		Reservation unrelatedReservation = new Reservation(new Attack(enemyPlanet, enemyPlanet2, 10), 2);
		Reservations reservations = new Reservations(unrelatedReservation);
		Universes universes = new Universes(new Planets(planet, enemyPlanet, enemyPlanet2), new Fleets(enemy), 10, 1, reservations);
		assertEquals(24, universes.sparableShips(planet));
	}

	@Test
	public void should_return_the_amount_remaining_after_considering_fleets_and_multiple_reservations() {
		Planet enemyPlanet = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet enemyPlanet2 = new Planet(id = 7, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 25, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 10, enemyPlanet.id, planet.id, 10, 2);
		Reservation reservation1 = new Reservation(new Attack(planet, enemyPlanet, 10), 2);
		Reservation reservation2 = new Reservation(new Attack(planet, enemyPlanet2, 10), 3);
		Reservations reservations = new Reservations(reservation1, reservation2);
		Universes universes = new Universes(new Planets(planet, enemyPlanet, enemyPlanet2), new Fleets(enemy), 10, 1, reservations);
		assertEquals(4, universes.sparableShips(planet));
	}
	
	@Test
	public void should_use_reservations_for_current_turn_to_decide_the_future() {
		Planet enemyPlanet = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 25, growthRate = 5, x = 5, y = 5);
		Reservation reservation1 = new Reservation(new Attack(planet, enemyPlanet, 10), 2);
		Reservations reservations = new Reservations(reservation1);
		Universes universes = new Universes(new Planets(planet, enemyPlanet), new Fleets(), 10, 2, reservations);
		assertEquals(1, universes.first().fleets.size());
	}
	
	@Test
	public void should_not_use_reservations_for_the_future_that_cannot_be_met() {
		Planet enemyPlanet = new Planet(id = 6, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 10, y = 10);
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 25, growthRate = 5, x = 5, y = 5);
		Reservation reservation1 = new Reservation(new Attack(planet, enemyPlanet, 50), 2);
		Reservations reservations = new Reservations(reservation1);
		Universes universes = new Universes(new Planets(planet, enemyPlanet), new Fleets(), 10, 2, reservations);
		assertEquals(0, universes.first().fleets.size());
	}
	
	/**
	 * neededToWin
	 */
	@Test
	public void should_return_the_number_needed_to_save_a_planet() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 55, 0, id, 10, 2);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy), 10, 1);
		assertEquals(6, universes.neededToWin(planet, 2));
	}

	@Test
	// TODO: fix
	public void should_return_the_number_needed_to_win_a_neutral_planet() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.NEUTRAL_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 20, 0, id, 10, 2);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy), 10, 1);
		assertEquals(21, universes.neededToWin(planet, 2));
	}
	
	@Test
	public void should_return_the_number_needed_to_save_a_planet_when_enemy_wins_and_support_planet_is_too_distant() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 55, 0, id, 10, 2);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy), 10, 1);
		assertEquals(6, universes.neededToWin(planet, 2));
	}

	@Test
	public void should_return_the_number_needed_to_win_an_enemy_planet() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 40, growthRate = 5, x = 5, y = 5);
		Universes universes = new Universes(new Planets(planet), new Fleets(), 10, 1);
		assertEquals(51, universes.neededToWin(planet, 2));
	}

	@Test
	public void should_return_the_number_needed_to_win_a_planet_from_a_given_distance() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet mine = new Fleet(PlanetWars.PLAYER_ID, 20, 0, id, 10, 1);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 40, 0, id, 10, 2);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy, mine), 10, 1);
		assertEquals(41, universes.neededToWin(planet, 4));
	}

	@Test
	public void should_figure_out_how_many_are_needed_to_win_before_a_planet_would_be_lost_and_retaken() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.PLAYER_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 40, 0, id, 10, 1);
		Fleet mine = new Fleet(PlanetWars.PLAYER_ID, 40, 0, id, 10, 3);
		Fleet enemy2 = new Fleet(PlanetWars.ENEMY_ID, 40, 0, id, 10, 5);
		Universes universes = new Universes(new Universe(new Planets(planet), new Fleets(enemy, enemy2, mine), 1), 10);
		assertEquals(25, universes.neededToWin(planet, 1));
	}
	
	@Test
	public void should_figure_out_how_many_are_needed_to_win_when_more_enemy_ships_are_coming() {
		Planet planet = new Planet(id = 5, owner = PlanetWars.ENEMY_ID, ships = 10, growthRate = 5, x = 5, y = 5);
		Fleet enemy = new Fleet(PlanetWars.ENEMY_ID, 10, 0, id, 10, 1);
		Fleet enemy2 = new Fleet(PlanetWars.ENEMY_ID, 15, 0, id, 10, 5);
		Universes universes = new Universes(new Planets(planet), new Fleets(enemy, enemy2), 10, 1);
		assertEquals(31, universes.neededToWin(planet, 2));
	}
	
	
}
