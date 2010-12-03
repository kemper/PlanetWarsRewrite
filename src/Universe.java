import java.util.ArrayList;
import java.util.List;


public class Universe {
	Planets planets;
	Fleets fleets;
	int turn;
	Reservations reservations;

	public Universe(Planets planets, Fleets fleets) {
		this(planets, fleets, 1);
	}
	
	public Universe(Planets planets, Fleets fleets, int turn) {
		this(planets, fleets, turn, new Reservations());
	}
	
	public Universe(Planets planets, Fleets fleets, int turn, Reservations reservations) {
		this.turn = turn;
		Fleets sentThisTurn = new Fleets();
		for(Reservation reservation : reservations.items) {
			if (turn == reservation.turn) {
				Planet planet = planets.find(reservation.attack.homePlanet.id);
				if (planet.isMine() && planet.numShips >= reservation.attack.ships) {
					sentThisTurn = sentThisTurn.plus(new Fleet(reservation.attack));
					planets = planets.remove(planet);
					planet = planet.removeShips(reservation.attack.ships);
					planets = planets.union(planet);
				}
			}
		}
		this.planets = planets;
		this.fleets = fleets.plus(sentThisTurn);
		this.reservations = reservations;
	}
	
	public Universe next() {
		List<Fleet> futureFleets = new ArrayList<Fleet>();
		List<Planet> newPlanets = new ArrayList<Planet>();
		for(Planet planet : planets.items) {
			newPlanets.add(planet.grow());
		}
		List<Fleet> arrivingFleets = new ArrayList<Fleet>();
		for(Fleet fleet : fleets.items) {
			Fleet movedFleet = fleet.move();
			if (movedFleet.turnsRemaining > 0) {
				futureFleets.add(movedFleet);
			} else {
				arrivingFleets.add(movedFleet);
			}
		}
		Fleets arriving = new Fleets(arrivingFleets);
		for(Fleet fleet : arrivingFleets) {
			Planet planet = new Planets(newPlanets).find(fleet.destinationPlanet);
			Fleets attacking = arriving.attacking(planet);
			if (attacking.enemyFleets().ships() != attacking.myFleets().ships()) {
				newPlanets.remove(planet);
				planet = planet.receive(fleet);
				newPlanets.add(planet);
			}
		}
		return new Universe(new Planets(newPlanets), new Fleets(futureFleets), turn + 1, reservations);
	}

	@Override
	public String toString() {
		return "Universe [fleets=" + fleets + ", planets=" + planets + ", reservations=" + reservations + ", turn="
				+ turn + "]";
	}

	Universe haveEveryPlanetAttack(Planet mine, Planets planetsToAttack) {
		List<Planet> newPlanets = new ArrayList<Planet>();
		List<Fleet> additionalFleets = new ArrayList<Fleet>();
		for(Planet p : planetsToAttack.minus(mine).items) {
			if(!p.isNeutral()) {
				newPlanets.add(p.removeShips(p.numShips));
				additionalFleets.add(p.attack(mine));
			}
		}
		return new Universe(planets.replace(new Planets(newPlanets)), fleets.union(new Fleets(additionalFleets)), turn, reservations);
	}
}
