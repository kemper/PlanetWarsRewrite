import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Universes extends PlanetWarsList<Universe, Universes> {

	private Map<Planet, Map<Integer, Integer>> cachedPlanets;

	public Universes(Universe universe, int stepsInTheFuture) {
		cachedPlanets = new HashMap<Planet, Map<Integer, Integer>>();
		items.add(universe);
		for(int step = 1; step <= stepsInTheFuture + 1; step++) {
			universe = universe.next();
			items.add(universe);
		}
	}
	
	public Universes(List<Universe> items) {
		super(items);
	}

	public Universes(Universe...items) {
		super(items);
	}

	public Universes(Planets planets, Fleets fleets, int stepsInTheFuture, int currentTurn) {
		this(new Universe(planets, fleets, currentTurn, new Reservations()), stepsInTheFuture);
	}
	
	public Universes(Planets planets, Fleets fleets, int stepsInTheFuture, int currentTurn, Reservations reservations) {
		this(new Universe(planets, fleets, currentTurn, reservations), stepsInTheFuture);
	}


	@Override
	Universes build(List<Universe> items) {
		return new Universes(items);
	}

	@Override
	Universes build(Universe... items) {
		return new Universes(items);
	}

	public int sparableShips(Planet planet) {
		int smallestAmount = planet.numShips;
		for (Universe universe : items) {
			Planet futurePlanet = universe.planets.find(planet.id);
			if (futurePlanet.isEnemy()) {
				smallestAmount = 0;
			} else if (futurePlanet.numShips < smallestAmount) {
				smallestAmount = futurePlanet.numShips;
			}
		}
		return smallestAmount > 0 ? smallestAmount - 1 : 0;
	}

	public int cachedNeededToWin(Planet planet, Planet homePlanet) {
		int distance = planet.distance(homePlanet);
		Map<Integer, Integer> cache = this.cachedPlanets.get(planet);
		if(cache != null && cache.containsKey(distance)) {
			return cache.get(distance);
		}
		int lastWinningAmount = neededToWin(planet, distance);
		if(cache == null) {
			cache = new HashMap<Integer, Integer>();
			cachedPlanets.put(planet, cache);
		}
		cache.put(distance, lastWinningAmount);
		return lastWinningAmount;
	}
	
	public int neededToWin(Planet planet, int distance) {
		Planet finalPlanet = last().planets.find(planet.id);
		if (finalPlanet.isMine()) return 0;
		int smallestAmount = 0;
		Planet futurePlanet;
		int lowerBound = 0;
		int lastWinningAmount = finalPlanet.numShips + 1;
		int upperBound = finalPlanet.numShips;
		do {
			smallestAmount = lowerBound + (int) Math.ceil((upperBound - lowerBound) / (double) 2);
			Universe universe = first();
			Fleets fleets = universe.fleets.plus(new Fleet(PlanetWars.PLAYER_ID, smallestAmount, 0, planet.id, distance, distance));
			Universes possibleUniverses = new Universes(new Universe(universe.planets, fleets, universe.turn), items.size());
			futurePlanet = possibleUniverses.last().planets.find(planet.id);
			if (futurePlanet.isMine()) {
				lastWinningAmount = smallestAmount;
				upperBound = smallestAmount - 1;
			} else {
				lowerBound = smallestAmount + 1;
			}
		} while (upperBound > lowerBound /*|| lastWinningAmount == 0 */);
		return lastWinningAmount;
	}

	public int unsafeNeededToWin(Planet planet, Planet homePlanet) {
		int distance = planet.distance(homePlanet);
		if (distance < items.size() + 1) {
			return items.get(distance + 1).planets.find(planet.id).numShips;
		}
		return 0;
	}

	public Planets myFuturePlanets() {
		return first().planets.findAll(last().planets.myPlanets().ids());
	}
	public Planets futureEnemyPlanets() {
		return first().planets.findAll(last().planets.enemyPlanets().ids());
	}

	public Planets futurePlanets() {
		return first().planets.findAll(last().planets.ids());
	}

	public Planets futureNeutralPlanets() {
		return first().planets.findAll(last().planets.neutralPlanets().ids());
	}
	
	public Universe futureOnceFleetsAreDone() {
		for(Universe universe : items) {
			if (universe.fleets.size() == 0) {
				return universe;
			}
		}
		return last();
	}

	public Planets planetsLost() {
		Planets mineAtSomePoint = new Planets();
		for(Universe universe : items) {
			mineAtSomePoint = mineAtSomePoint.union(universe.planets.myPlanets());
		}
		Planets lost = mineAtSomePoint.minus(mineAtSomePoint.minus(futureEnemyPlanets()));
		return first().planets.findAll(lost.ids());
	}

	public Planets planetsBeingSniped() {
		Set<Integer> ids = first().fleets.myFleets().destinationIds();
		return first().planets.findAll(futurePlanets().findAll(ids).enemyPlanets().ids());
	}

}
