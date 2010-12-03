import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Planets extends PlanetWarsList<Planet, Planets> {

	@Override
	Planets build(List<Planet> items) {
		return new Planets(items);
	}

	@Override
	Planets build(Planet... items) {
		return new Planets(items);
	}

	public Planets findAll(Set<Integer> ids) {
		List<Planet> found = new ArrayList<Planet>();
		for (Integer id : ids) {
			found.add(find(id));
		}
		return build(found);
	}

	Planet find(int id) {
		for (Planet p : this.items) {
			if (p.id == id)
				return p;
		}
		return null;
	}

	boolean contains(int id) {
		for (Planet t : items) {
			if (t.id == id) {
				return true;
			}
		}
		return false;
	}

	public Set<Integer> ids() {
		Set<Integer> ids = new HashSet<Integer>();
		for (Planet p : items) {
			ids.add(p.id);
		}
		return ids;
	}

	public Planets(Collection<Planet> planets) {
		super(planets);
	}

	public Planets(Planet... planets) {
		super(planets);
	}

	int minimumDistanceFrom(Planet target) {
		int distance = Integer.MAX_VALUE;
		for (Planet planet : items) {
			if (distance(planet, target) < distance) {
				distance = distance(planet, target);
			}
		}
		return distance;
	}

	double averageDistanceToPlanets(Planet planet) {
		if (items.size() == 0)
			return Double.MAX_VALUE;
		double totalDistance = 0;
		for (Planet other : items) {
			totalDistance += distance(other, planet);
		}
		return totalDistance / items.size();
	}

	int minDistanceToPlanets(Planet planet, List<Planet> planets) {
		int minDistance = Integer.MAX_VALUE;
		if (planets.size() > 0) {
			for (Planet other : planets) {
				if (minDistance > distance(planet, other)) {
					minDistance = distance(planet, other);
				}
			}
		}
		return minDistance;
	}

	int averageDistanceFrom(Planets planets) {
		int average = 0;
		for (Planet planet : planets.items) {
			average += averageDistanceToPlanets(planet);
		}
		return average / planets.size();
	}

	Planets sortByMinimumDistanceTo(final Planets planet) {
		List<Planet> sorted = new ArrayList<Planet>(items);
		Collections.sort(sorted, new Comparator<Planet>() {
			@Override
			public int compare(Planet o1, Planet o2) {
				int d1 = distance(o1, planet.closestTo(o1));
				int d2 = distance(o2, planet.closestTo(o2));
				return new Integer(d1).compareTo(new Integer(d2));
			}

		});
		return new Planets(sorted);
	}

	int minDistanceTo(Planet planet) {
		int minDistance = 0;
		for (Planet other : items) {
			if (minDistance > distance(planet, other)) {
				minDistance = distance(planet, other);
			}
		}
		return minDistance;
	}

	int maxDistanceTo(Planet planet) {
		int maxDistance = 0;
		for (Planet other : items) {
			if (maxDistance < distance(planet, other)) {
				maxDistance = distance(planet, other);
			}
		}
		return maxDistance;
	}

	Planet largest() {
		if (items.isEmpty())
			return null;
		Planet planet = items.get(0);
		for (Planet p : items) {
			if (p.numShips > planet.numShips) {
				planet = p;
			}
		}
		return planet;
	}

	Planet closestTo(Planet planet) {
		Planet closest = null;
		for (Planet other : items) {
			if (closest == null || distance(planet, other) < distance(planet, closest)) {
				closest = other;
			}
		}
		return closest;
	}

	int distance(Planet source, Planet destination) {
		double dx = source.x - destination.x;
		double dy = source.y - destination.y;
		return (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
	}

	int maxGrowthRate() {
		int rate = Integer.MIN_VALUE;
		for (Planet planet : items) {
			if (planet.growthRate > rate) {
				rate = planet.growthRate;
			}
		}
		return rate;
	}

	int production() {
		int prod = 0;
		for (Planet planet : items) {
			prod += planet.growthRate;
		}
		return prod;
	}

	int maximumDistanceBetweenAnyTwoPlanets() {
		int distance = 0;
		for (Planet planet1 : items) {
			for (Planet planet2 : items) {
				if (distance(planet1, planet2) > distance) {
					distance = distance(planet1, planet2);
				}
			}
		}
		return distance;
	}

	Planets allCloserTo1Than2(Planet planet1, Planet planet2) {
		List<Planet> closest = new ArrayList<Planet>();
		for (Planet planet : items) {
			if (distance(planet2, planet) >= distance(planet1, planet)) {
				closest.add(planet);
			}
		}
		return new Planets(closest);
	}

	Planets otherPlanetsWithinRadius(Planet planet, int distance) {
		List<Planet> closest = new ArrayList<Planet>();
		for (Planet other : items) {
			if (!planet.equals(other) && distance(planet, other) < distance) {
				closest.add(other);
			}
		}
		return new Planets(closest);
	}

	Planets sortByDistanceTo(final Planet planet) {
		List<Planet> sortedPlanets = new ArrayList<Planet>(items);
		Collections.sort(sortedPlanets, new Comparator<Planet>() {
			@Override
			public int compare(Planet o1, Planet o2) {
				Integer distance1 = distance(planet, o1);
				Integer distance2 = distance(planet, o2);
				return distance1.compareTo(distance2);
			}
		});
		return new Planets(sortedPlanets);
	}

	int ships() {
		int ships = 0;
		for (Planet planet : items) {
			ships += planet.numShips;
		}
		return ships;
	}

	Planets sortByRescueValue() {
		List<Planet> sortedPlanets = new ArrayList<Planet>(items);
		Collections.sort(sortedPlanets, new Comparator<Planet>() {
			@Override
			public int compare(Planet p1, Planet p2) {
				return new Integer(value(p2)).compareTo(new Integer(value(p1)));
			}

			private int value(Planet planet) {
				double growthRatio = planet.growthRate / (double) maxGrowthRate();
				int graphDistanceValue = (int) (50 - (50 * (minus(planet).minDistanceTo(planet) / maximumDistanceBetweenAnyTwoPlanets())));
				int growthValue = (int) (100 * growthRatio);
				int value = graphDistanceValue + growthValue;
				return value;
			}
		});
		return new Planets(sortedPlanets);
	}

	Planets sortByGrowthPotential(final Planet planet, final int maxTurns) {
		List<Planet> sortedPlanets = new ArrayList<Planet>(items);
		Collections.sort(sortedPlanets, new Comparator<Planet>() {
			@Override
			public int compare(Planet o1, Planet o2) {
				Integer potential1 = (maxTurns - distance(planet, o1)) * o1.growthRate - o1.numShips;
				Integer potential2 = (maxTurns - distance(planet, o2)) * o2.growthRate - o2.numShips;
				return potential2.compareTo(potential1);
			}
		});
		return new Planets(sortedPlanets);
	}

	Planets beingAttackedBy(Fleets fleets) {
		List<Planet> planets = new ArrayList<Planet>();
		for (Planet p : planets) {
			if (fleets.fleetsAttacking(p).size() > 0) {
				planets.add(p);
			}
		}
		return new Planets(planets);
	}

	public Planets sortByAverageDistanceTo(final Planets planets) {
		List<Planet> sorted = this.items();
		Collections.sort(sorted, new Comparator<Planet>() {
			@Override
			public int compare(Planet o1, Planet o2) {
				return new Double(planets.averageDistanceToPlanets(o1)).compareTo(new Double(planets
						.averageDistanceToPlanets(o2)));
			}
		});
		return new Planets(sorted);
	}

	public Planets myPlanets() {
		List<Planet> myPlanets = new ArrayList<Planet>();
		for (Planet planet : items) {
			if (planet.owner == PlanetWars.PLAYER_ID) {
				myPlanets.add(planet);
			}
		}
		return new Planets(myPlanets);
	}

	public Planets neutralPlanets() {
		List<Planet> myPlanets = new ArrayList<Planet>();
		for (Planet planet : items) {
			if (planet.owner == PlanetWars.NEUTRAL_ID) {
				myPlanets.add(planet);
			}
		}
		return new Planets(myPlanets);
	}

	public Planets enemyPlanets() {
		List<Planet> enemyPlanets = new ArrayList<Planet>();
		for (Planet planet : items) {
			if (planet.owner == PlanetWars.ENEMY_ID) {
				enemyPlanets.add(planet);
			}
		}
		return new Planets(enemyPlanets);
	}

	public Planets rejectWithinSafeZone(Planets planets) {
		if (planets.empty()) {
			return planets;
		}
		Planets remaining = new Planets();
		for (Planet p : this.items) {
			if (remaining.size() == 0) {
				remaining = remaining.union(p);
			} else {
				int distance = p.distance(planets.closestTo(p));
				int otherDistance = p.distance(remaining.remove(p).closestTo(p));
				if (distance < otherDistance) {
					remaining = remaining.union(p);
				}
			}
		}
		return remaining;
	}

	public Planets sortByLargest() {
		List<Planet> sortedPlanets = new ArrayList<Planet>(items);
		Collections.sort(sortedPlanets, new Comparator<Planet>() {
			@Override
			public int compare(Planet o1, Planet o2) {
				Integer ships1 = o1.numShips;
				Integer ships2 = o2.numShips;
				return ships2.compareTo(ships1);
			}
		});
		return new Planets(sortedPlanets);
	}

	public Planets closestTo(Planets planets) {
		Set<Planet> closest = new HashSet<Planet>();
		if (size() > 0) {
			for (Planet planet : planets.items) {
				closest.add(closestTo(planet));
			}
		}
		return new Planets(closest);
	}

	public Map<Planet, Set<Planet>> proximityGroupsSet(Planets planets) {
		HashMap<Planet, Set<Planet>> hashMap = new HashMap<Planet, Set<Planet>>();
		if (size() > 0) {
			for (Planet planet : planets.items) {
				Planet findClosestTo = closestTo(planet);
				if (hashMap.get(findClosestTo) == null) {
					Set<Planet> hashSet = new HashSet<Planet>();
					hashSet.add(planet);
					hashMap.put(findClosestTo, hashSet);
				} else {
					Set<Planet> hashSet = hashMap.get(findClosestTo);
					hashSet.add(planet);
				}
			}
		}
		return hashMap;
	}

	public Map<Planet, Planets> groupByClosest(Planets planets) {
		Map<Planet, Set<Planet>> hashMap = proximityGroupsSet(planets);
		Map<Planet, Planets> hashMap2 = new HashMap<Planet, Planets>();
		for (Planet p : hashMap.keySet()) {
			hashMap2.put(p, new Planets(hashMap.get(p)));
		}
		return hashMap2;
	}

	public Planets closerTo1Than2(Planets one, Planets two) {
		List<Planet> closer = new ArrayList<Planet>();
		for (Planet p : items) {
			if (one.minimumDistanceFrom(p) < two.minimumDistanceFrom(p)) {
				closer.add(p);
			}
		}
		return new Planets(closer);
	}

	public Planets atDistance(Planet planet, int distance) {
		List<Planet> atDistance = new ArrayList<Planet>();
		for (Planet p : items) {
			if (p.distance(planet) == distance) {
				atDistance.add(p);
			}
		}
		return new Planets(atDistance);
	}

	public Planets closestPlanetsThatCouldBeat(Planet planet) {
		Planets planets = this.sortByDistanceTo(planet);
		Planets neededToBeat = new Planets();
		for (Planet p : planets.items) {
			if (neededToBeat.ships() > planet.numShips)
				break;
			neededToBeat = neededToBeat.union(p);
		}
//		if (neededToBeat.ships() < planet.numShips)
//			return new Planets();
		return neededToBeat;
	}

	public Planets grow() {
		List<Planet> planets = new ArrayList<Planet>();
		for (Planet planet : items) {
			planets.add(planet.grow());
		}
		return new Planets(planets);
	}

}
