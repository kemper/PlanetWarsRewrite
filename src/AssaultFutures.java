import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssaultFutures {
	HashMap<Planet, Universes> map = new HashMap<Planet, Universes>();
	private Universe universe;
	private AttackType type;
	
	public AssaultFutures(Universe universe) {
		this(universe, AttackType.All);
	}
	
	public AssaultFutures(Universe universe, AttackType type) {
		this.universe = universe;
		this.type = type;
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		for (Planet p : map.keySet()) {
			string.append(p.toString());
			string.append(" => ");
			string.append(map.get(p).toString());
		}
		return string.toString();
	}

	private void create(Planet planet, int distance) {
		if(!map.containsKey(planet)) {
			map.put(planet, possibleState(universe, planet, distance));
		} else {
			Universes universes = map.get(planet);
			if (universes.size() <= distance) {
				Universes next = possibleState(universes.last(), planet, distance - universes.size() + 1);
				map.put(planet, universes.plus(next));
			}
		}
	}

	public Universes possibleState(Universe universe, Planet mine, int numberOfTurns) {
		List<Universe> universes = new ArrayList<Universe>();
		universes.add(universe);
		for (int step = 1; step <= numberOfTurns; step++) {
			universe = universe.haveEveryPlanetAttack(mine, type.planets(universe.planets)).next();
			universes.add(universe);
		}
		return new Universes(universes);
	}

	public Planet get(Planet p) {
		create(p, universe.planets.maximumDistanceBetweenAnyTwoPlanets());
		return map.get(p).last().planets.find(p.id);
	}

	public Planet get(Planet planet, int distance) {
		create(planet, distance);
		return map.get(planet).items.get(distance).planets.find(planet.id);
	}
}
