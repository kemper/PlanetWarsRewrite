import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fleets extends PlanetWarsList<Fleet, Fleets>{

	public Fleets(List<Fleet> fleets) {
		super(fleets);
	}
	
	public Fleets(Fleet... fleets) {
		super(fleets);
	}

	@Override
	Fleets build(List<Fleet> items) {
		return new Fleets(items);
	}

	@Override
	Fleets build(Fleet... items) {
		return new Fleets(items);
	}

	public List<Fleet> findByTurnsRemaining(int turnsRemaining) {
		List<Fleet> attacking = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (fleet.turnsRemaining == turnsRemaining) {
				attacking.add(fleet);
			}
		}
		return attacking;
	}

	int shipsAttacking(Planet homePlanet) {
		int attacking = 0;
		for (Fleet fleet : fleetsAttacking(homePlanet).items) {
			attacking += fleet.numShips;
		}
		return attacking;
	}

	Fleets fleetsAttacking(Planet p) {
		List<Fleet> attacking = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (fleet.destinationPlanet == p.id) {
				attacking.add(fleet);
			}
		}
		return new Fleets(attacking);
	}

	int distanceToClosest(Planet planet) {
		int closestFleet = 0;
		if (!items.isEmpty()) {
			closestFleet = items.get(0).turnsRemaining;
			for (Fleet fleet : items) {
				if (fleet.turnsRemaining < closestFleet) {
					closestFleet = fleet.turnsRemaining;
				}
			}
		}
		return closestFleet;
	}

	int distanceOfNearestAttackingFleet(Planet homePlanet) {
		int distance = Integer.MAX_VALUE;
		for (Fleet fleet : fleetsAttacking(homePlanet).items) {
			if (fleet.turnsRemaining < distance)
				distance = fleet.turnsRemaining;
		}
		return distance;
	}

	Fleets sortByTurnsRemaining() {
		List<Fleet> sortedFleets = new ArrayList<Fleet>(items);
		Collections.sort(sortedFleets, new Comparator<Fleet>() {
			@Override
			public int compare(Fleet o1, Fleet o2) {
				if (o1.turnsRemaining == o2.turnsRemaining) {
					return new Integer(o1.owner).compareTo(o2.owner);
				} else {
					return new Integer(o1.turnsRemaining).compareTo(o2.turnsRemaining);
				}
			}
		});
		return new Fleets(sortedFleets);
	}

	int ships() {
		int ships = 0;
		for (Fleet fleet : items) {
			ships += fleet.numShips;
		}
		return ships;
	}

	public void update(Fleet newFleet) {
		items.add(newFleet);
	}

	Fleets enemyFleets() {
		List<Fleet> fleets = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (fleet.owner != 1) {
				fleets.add(fleet);
			}
		}
		return new Fleets(fleets);
	}
	
	public Fleets myFleets() {
		List<Fleet> fleets = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (fleet.owner == PlanetWars.PLAYER_ID) {
				fleets.add(fleet);
			}
		}
		return new Fleets(fleets);
	}

	public Fleets sliceByTurnsRemaining(int start, int end) {
		List<Fleet> fleets = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (start <= fleet.turnsRemaining && fleet.turnsRemaining <= end) {
				fleets.add(fleet);
			}
		}
		return new Fleets(fleets);
	}

	public Fleets sentBy(Planet planet) {
		List<Fleet> sent = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (fleet.sourcePlanet == planet.id) {
				sent.add(fleet);
			}
		}
		return new Fleets(sent);
	}

	public Set<Integer> destinationIds() {
		Set<Integer> ids = new HashSet<Integer>();
		for (Fleet fleet : items) {
			ids.add(fleet.destinationPlanet);
		}
		return ids;
	}

	public Fleets attacking(Planets planets) {
		Set<Integer> ids = planets.ids();
		List<Fleet> attacking = new ArrayList<Fleet>();
		for(Fleet fleet : items) {
			if(ids.contains(fleet.destinationPlanet)) {
				attacking.add(fleet);
			}
		}
		return new Fleets(attacking);
	}

	public Fleets attacking(Planet mine) {
		return attacking(new Planets(mine));
	}

	public Fleets canBeResentAfterArrival(int turnsRemaining, int newDistance, int newTarget) {
		List<Fleet> sent = new ArrayList<Fleet>();
		for (Fleet fleet : items) {
			if (turnsRemaining >= fleet.turnsRemaining + newDistance) {
				sent.add(new Fleet(fleet.owner, fleet.numShips, fleet.destinationPlanet,
						newTarget, newDistance, newDistance));
			}
		}
		return new Fleets(sent);
	}

}
