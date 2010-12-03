import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanetWars {

	public static final int NEUTRAL_ID = 0;
	public static final int PLAYER_ID = 1;
	public static final int ENEMY_ID = 2;

	private List<Planet> planetz;
	private List<Fleet> fleetz;
	private List<Attack> attacks = new ArrayList<Attack>();
	private static int currentTurn = 0;
	private Fleets fleets;
	private Planets planets;
	private Fleets myFleets;
	Planets myPlanets;
	Planets enemyPlanets;
	private Universes universes;
	private Fleets enemyFleets;
	private Planets neutralPlanets;
	private Planets mostStrategicPlanets;
	private Planets fairlyStrategicEnemyPlanets;
	private Planets planetsLost;
	private static Reservations reservations = new Reservations();
	private static int reservationGroupId = 1;
	private static Set<Integer> previousHomePlanetIds = new HashSet<Integer>();

	PlanetWars(String gameStateString) {
		currentTurn++;
		planetz = new ArrayList<Planet>();
		fleetz = new ArrayList<Fleet>();
		parseGameState(gameStateString);
		fleets = new Fleets(fleetz);
		planets = new Planets(planetz);
		myFleets = fleets.myFleets();
		myPlanets = planets.myPlanets();
		enemyPlanets = planets.enemyPlanets();
		enemyFleets = fleets.enemyFleets();
		neutralPlanets = planets.neutralPlanets();
		universes = new Universes(planets, fleets, planets.maximumDistanceBetweenAnyTwoPlanets() + 1, currentTurn, reservations);
		planetsLost = universes.planetsLost();
		mostStrategicPlanets = mostStrategicPlanets();
		fairlyStrategicEnemyPlanets = fairlyStrategicEnemyPlanets();
		previousHomePlanetIds.addAll(myPlanets.ids());
	}


	public PlanetWars(List<Planet> planets, List<Fleet> enemyFleet) {
		this.planetz = planets;
		this.fleetz = enemyFleet;
	}

	private void addAttack(int reservationGroupId, Planet home, Planet target, int ships) {
		Attack attack = new Attack(reservationGroupId, home, target, ships);
		Fleet newFleet = new Fleet(home.owner, ships, home.id, target.id, home.distance(target),
				home.distance(target));
		fleets.update(newFleet);
		myFleets.update(newFleet);

		if (ships != 0) {
			attacks.add(attack);
		}
	}

	private void rescue(Planets rescuable) {
		for(Planet p : rescuable.items) {
			Planets myPlanetz = myPlanets.sortByDistanceTo(p);
			attackOneTarget(myPlanetz, p);
		}
	}
	
	private void processReservations() {
		List<Integer> canceledReservations = new ArrayList<Integer>();
		for(Reservation r : reservations.items) {
			Planet homePlanet = planets.find(r.attack.homePlanet.id);
			Planet target = planets.find(r.attack.target.id);
			if (r.turn == currentTurn && homePlanet.isMine() && !r.processed) {
				int shipsRemaining = homePlanet.numShips - shipsUsedToAttack(homePlanet);
				if(shipsRemaining >= r.attack.ships) {
					addAttack(r.reservationGroupId, homePlanet, target, r.attack.ships);
				} else {
					canceledReservations.add(r.reservationGroupId);
				}
				r.processed = true;
			}
		}
		cancelReservations(canceledReservations);
		reservations.purge(currentTurn);
	}

	private void cancelReservations(List<Integer> canceledReservations) {
		List<Attack> attackz = new ArrayList<Attack>(attacks);
		List<Reservation> reservationz = new ArrayList<Reservation>(reservations.items);
		for(Integer id : canceledReservations) {
			for(Attack attack : attackz) {
				if(attack.reservationGroupId == id) {
					attacks.remove(attack);
				}
			}
			for(Reservation reservation : reservationz) {
				if(reservation.reservationGroupId == id) {
					reservations.cancel(reservation);
				}
			}
		}
	}

	private void move(Map<Planet, Planets> groups) {
		for (Planet planet : groups.keySet()) {
			for (Planet helper : groups.get(planet).items) {
				Integer safelySparableShips = safelySparableShips(helper, null);
				if (safelySparableShips > 0 && !(mostStrategicPlanets.contains(planet) && mostStrategicPlanets.contains(helper))) {
					addAttack(0, helper, planet, safelySparableShips);
				}
			}
		}
	}
	
	private Planets mostStrategicPlanetsInNeed() {
		Planets mostStrategic = mostStrategicPlanets();
		Planets inNeed = new Planets();
		for (Planet planet : mostStrategic.items) {
			if (safelySparableShips(planet, null) == 0) {
				inNeed = inNeed.union(planet);
			}
		}
		return inNeed;
	}


	private void attack(Planets attackable) {
		attackable = sortByValue(attackable);
		int numberOfSkips = 0;
		for(Planet p : attackable.items) {
			Planets myPlanetz = myPlanets.sortByDistanceTo(p).slice(0,2);
			boolean didAttack = attackOneTarget(myPlanetz, p);
			if (!didAttack) {
				numberOfSkips++;
			}
		}
	}

	private Planets fairlyStrategicEnemyPlanets() {
		Planets closestFuturePlanetsToMe = universes.futureEnemyPlanets().closestTo(universes.myFuturePlanets());
		Planets closestPlanetsToMe = enemyPlanets.closestTo(myPlanets);
		Planets planets = closestFuturePlanetsToMe.union(closestPlanetsToMe);
		return planets.unique().rejectWithinSafeZone(myPlanets.union(universes.myFuturePlanets())).sortByRescueValue();
	}
	
	private Planets attackable() {
		return planets;
	}

	private Planets rescuable() {
		return myPlanets.minus(universes.myFuturePlanets()).sortByRescueValue();
	}
	
	private Planets mostStrategicPlanets() {
		Planets certainFuturePlanets = universes.myFuturePlanets().minus(enemyPlanets);
		Planets closestFuturePlanetsToTheEnemy = certainFuturePlanets.closestTo(universes.futureEnemyPlanets());
		Planets closestPlanetsToTheEnemy = myPlanets.closestTo(enemyPlanets);
		Planets planets = closestFuturePlanetsToTheEnemy.union(closestPlanetsToTheEnemy);
		return planets.sortByMinimumDistanceTo(enemyPlanets)
					  .rejectWithinSafeZone(enemyPlanets.union(universes.futureEnemyPlanets())).sortByRescueValue();
	}
	
	private Planets mostStrategicPlanetsForGrowth() {
		Planets futurePlanets = universes.futurePlanets();
		Planets strategic = myPlanets.closestTo(futurePlanets.minus(futurePlanets.myPlanets()));
		return strategic.sortByMinimumDistanceTo(enemyPlanets)
						.rejectWithinSafeZone(universes.futureEnemyPlanets()).sortByRescueValue();
	}
	
	private boolean attackOneTarget(Planets myPlanetz, Planet target) {
		Planets planetsNeeded = new Planets();
		int totalSparable = 0;
		int neededToWin = Integer.MAX_VALUE;
		for (Planet homePlanet : myPlanetz.items) {
			if (homePlanet.equals(target)) continue;
			neededToWin = shipsNeededToWin(homePlanet, target);
			if (neededToWin == 0) {
				break;
			}
			int shipsToSend = safelySparableShips(homePlanet, target);
			if (shipsToSend > 0) {
				totalSparable += shipsToSend;
				planetsNeeded = planetsNeeded.union(new Planets(homePlanet));
			}
			if (totalSparable > neededToWin) {
				break;
			}
		}
		if (totalSparable > neededToWin) {
			if (goodAttackGroup(planetsNeeded, target)) {
				addReservations(target, planetsNeeded);
				return true;
			} else {
				addFutureReservations(target, planetsNeeded);
				return true;
			}
		}
		return false;
	}


	private boolean goodAttackGroup(Planets planetsNeeded, Planet target) {
		if (planetsNeeded.size() == 0) return false;
		if(attackWouldBeBetterToWait(planetsNeeded, target)) {
			return false;
		}
		return true;
	}

	private boolean attackWouldBeBetterToWait(Planets planetsNeeded, Planet target) {
		Planets bestKnown = betterAttackGroup(planetsNeeded, target);
		if (bestKnown.size() < planetsNeeded.size()) {
			return true;
		}
		return false;
	}
	
	private Planets betterAttackGroup(Planets planetsNeeded, Planet target) {
		planetsNeeded = planetsNeeded.sortByDistanceTo(target);
		Planets bestKnown = planetsNeeded;
		Planets growingPlanets = planetsNeeded;
		int turnsUntilWin = planetsNeeded.maxDistanceTo(target);
		for(int x = 0; x < turnsUntilWin; x++) {
			growingPlanets = growingPlanets.grow();
			Planet last = planetsNeeded.find(growingPlanets.last().id);
			if(safelySparableShips(growingPlanets.minus(growingPlanets.last()), target) >= shipsNeededToWin(last, target)) {
				bestKnown = bestKnown.minus(growingPlanets.last());
				growingPlanets = growingPlanets.minus(growingPlanets.last());
			}
		}
		return bestKnown;
	}

	private Integer safelySparableShips(Planets planets, Planet target) {
		int sparable = 0;
		for(Planet p : planets.items) {
			sparable += safelySparableShips(p, target);
		}
		return sparable;
	}

	private void addReservations(Planet target, Planets planetsNeeded) {
		planetsNeeded = planetsNeeded.reverse();
		int totalSent = 0;
		for (Planet homePlanet : planetsNeeded.items) {
			Integer shipsNeededToWin = shipsNeededToWin(homePlanet, target);
			int remainingNeeded = shipsNeededToWin - totalSent;
			if (remainingNeeded <= 0) break;
			int sparableShips = safelySparableShips(homePlanet, target);
			int distanceDifference = planetsNeeded.first().distance(target) - homePlanet.distance(target);
			int shipsToSend = sparableShips > remainingNeeded ? remainingNeeded : sparableShips;
			reservations = reservations.plus(new Reservation(reservationGroupId, new Attack(homePlanet, target, shipsToSend), currentTurn, distanceDifference));
			totalSent += shipsToSend;
		}
		reservationGroupId++;
	}
	
	private void addFutureReservations(Planet target, Planets planetsNeeded) {
		planetsNeeded = planetsNeeded.reverse();
		Planets betterAttackGroup = betterAttackGroup(planetsNeeded, target);
		int turnsAhead = 0;
		if(betterAttackGroup.size() < planetsNeeded.size()) {
			turnsAhead = planetsNeeded.maxDistanceTo(target) - betterAttackGroup.maxDistanceTo(target);
			planetsNeeded = betterAttackGroup.reverse();
		}

		int totalSent = 0;
		for (Planet homePlanet : planetsNeeded.items) {
			Integer shipsNeededToWin = shipsNeededToWin(homePlanet, target);
			int remainingNeeded = shipsNeededToWin - totalSent;
			if (remainingNeeded <= 0) break;
			int sparableShips = safelySparableShips(homePlanet, target);
			int distanceDifference = planetsNeeded.first().distance(target) - homePlanet.distance(target);
			int shipsToSend = sparableShips + turnsAhead * homePlanet.growthRate;
			shipsToSend = shipsToSend > shipsNeededToWin ? shipsNeededToWin : shipsToSend;
			reservations = reservations.plus(new Reservation(reservationGroupId, new Attack(homePlanet, target, shipsToSend), currentTurn, distanceDifference + turnsAhead));
			totalSent += shipsToSend;
		}
		reservationGroupId++;
	}


	private Integer shipsNeededToWin(Planet homePlanet, Planet target) {
		int almostNeededToWin = universes.cachedNeededToWin(target, homePlanet);
		int neededToWin = almostNeededToWin 
							+ enemyPlanets.otherPlanetsWithinRadius(target, homePlanet.distance(target)).ships()
							- closerShipsAttackingThisTurn(homePlanet, target);
		neededToWin = neededToWin < 0 ? 0 : neededToWin;
		return neededToWin;
	}
	
	private int safelySparableShips(Planet homePlanet, Planet target) {
		int available = 0;
		Planet closestEnemy = enemyPlanets.closestTo(homePlanet);
		if (!closestEnemy.equals(target) && mostStrategicPlanets.contains(homePlanet)) {
			available = safeAmountToSpare(homePlanet, target);
		} else {
			available = universes.sparableShips(homePlanet) - shipsUsedThisTurn(homePlanet);
		}
		return available > 0 ? available : 0;
	}
	
	private int shipsUsedThisTurn(Planet homePlanet) {
		int used = shipsUsedToAttack(homePlanet);
		for (Reservation reservation : reservations.items) {
			if (!reservation.processed && reservation.turn == currentTurn && reservation.attack.homePlanet.equals(homePlanet)) {
				used += reservation.attack.ships;
			}
		}
		return used;
	}
	
	private int closerShipsAttackingThisTurn(Planet homePlanet, Planet target) {
		int used = 0;
		for (Reservation reservation : reservations.items) {
			if (!reservation.processed 
					&& reservation.turn == currentTurn 
					&& reservation.attack.target.equals(target) 
					&& homePlanet.distance(target) >= reservation.attack.homePlanet.distance(target)) {
				used += reservation.attack.ships;
			}
		}
		for (Attack attack : attacks) {
			if (attack.target.equals(target) 
					&& homePlanet.distance(target) >= attack.homePlanet.distance(target)) {
				used += attack.ships;
			}
		}
		return used;
	}

	private int shipsUsedToAttack(Planet homePlanet) {
		int used = 0;
		for (Attack attack : attacks) {
			if (attack.homePlanet.equals(homePlanet)) {
				used += attack.ships;
			}
		}
		return used;
	}

	private int safeAmountToSpare(Planet homePlanet, Planet target) {
		int initiallySparable = universes.sparableShips(homePlanet) - shipsUsedThisTurn(homePlanet);
		Map<Planet, Planets> groups = mostStrategicPlanets.groupByClosest(enemyPlanets);
		int rescueDifference = 0;
		if (groups.containsKey(homePlanet)) {
			Planets onesToStandAgainst = groups.get(homePlanet);
			if (!onesToStandAgainst.empty()) {
				Planet closestToWorryAbout = onesToStandAgainst.closestTo(homePlanet);
				if (!closestToWorryAbout.equals(target)) {
					rescueDifference = homePlanet.growthRate * closestToWorryAbout.distance(homePlanet) - closestToWorryAbout.numShips;
				}
			}
		}
		rescueDifference = rescueDifference > 0 ? 0 : rescueDifference;
		int amountToSpare = initiallySparable + rescueDifference;
		amountToSpare = amountToSpare > homePlanet.numShips ? homePlanet.numShips : amountToSpare;
		return amountToSpare > 0 ? amountToSpare : 0;
	}
	
	List<Attack> attackOrders() {
		processReservations();
		if(fleets.enemyFleets().attacking(myPlanets).size() == 0 
				&& fleets.myFleets().attacking(enemyPlanets).size() == 0
				&& universes.futurePlanets().closestTo(myPlanets).enemyPlanets().size() == 0) {
			mostStrategicPlanets = mostStrategicPlanetsForGrowth();
		}
		rescue(rescuable());
		attack(attackable());
		rescue(planetsLost);
		move(moveGroups());
		processReservations();
		return attacks;
	}

	private Map<Planet, Planets> moveGroups() {
		Planets mostStrategicPlanetz = mostStrategicPlanetsInNeed();
		if (planetz.size() == 0) {
			mostStrategicPlanetz = mostStrategicPlanets;
		}
		Map<Planet, Planets> groups = mostStrategicPlanetz.groupByClosest(myPlanets.minus(mostStrategicPlanetz));
		return groups;
	}

	private Planets sortByValue(Planets targets) {
		List<Planet> sortedPlanets = new ArrayList<Planet>(targets.items);
		Collections.sort(sortedPlanets, new Comparator<Planet>() {
			@Override
			public int compare(Planet p1, Planet p2) {
				return new Integer(attackValue(p2)).compareTo(new Integer(attackValue(p1)));
			}
		});
		return new Planets(sortedPlanets);
	}
	
	private int attackValue(Planet planet) {
		double growthRatio = planet.growthRate / (double) planets.maxGrowthRate();
		double myDistanceRatio = myPlanets.averageDistanceToPlanets(planet)
				/ (double) planets.maximumDistanceBetweenAnyTwoPlanets();
		int enemyValue = 0;
		if (planet.isEnemy()) {
			enemyValue = 50;
			if (fairlyStrategicEnemyPlanets.contains(planet)) {
				enemyValue = 100;
			}
		}
		
		int retakeValue = 0;
		if (previousHomePlanetIds.contains(planet.id)) {
			retakeValue = 100;
		}

		int myDistanceValue = 200 - (int) (200 * myDistanceRatio);
		int cost = (planet.numShips + enemyFleets.shipsAttacking(planet) - myFleets.shipsAttacking(planet));
		int growthValue = (int) (150 * growthRatio);

		int poorRatioCost = 0;
		if (neutralPlanets.contains(planet) && planet.numShips / (planet.growthRate + 1) > 20) {
			poorRatioCost = 500;
		}

		int value = retakeValue + myDistanceValue + growthValue + enemyValue - cost - poorRatioCost;
		return value;
	}

	void IssueOrder(Planet source, Planet dest, int numShips) {
		System.out.println("" + source.id + " " + dest.id + " " + numShips);
		System.out.flush();
	}

	void finishTurn() {
		System.out.println("go");
		System.out.flush();
	}

	private int parseGameState(String s) {
		planetz.clear();
		fleetz.clear();
		int planetID = 0;
		String[] lines = s.split("\n");
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			int commentBegin = line.indexOf('#');
			if (commentBegin >= 0) {
				line = line.substring(0, commentBegin);
			}
			if (line.trim().length() == 0) {
				continue;
			}
			String[] tokens = line.split(" ");
			if (tokens.length == 0) {
				continue;
			}
			if (tokens[0].equals("P")) {
				if (tokens.length != 6) {
					return 0;
				}
				double x = Double.parseDouble(tokens[1]);
				double y = Double.parseDouble(tokens[2]);
				int owner = Integer.parseInt(tokens[3]);
				int numShips = Integer.parseInt(tokens[4]);
				int growthRate = Integer.parseInt(tokens[5]);
				Planet p = new Planet(planetID++, owner, numShips, growthRate, x, y);
				planetz.add(p);
			} else if (tokens[0].equals("F")) {
				if (tokens.length != 7) {
					return 0;
				}
				int owner = Integer.parseInt(tokens[1]);
				int numShips = Integer.parseInt(tokens[2]);
				int source = Integer.parseInt(tokens[3]);
				int destination = Integer.parseInt(tokens[4]);
				int totalTripLength = Integer.parseInt(tokens[5]);
				int turnsRemaining = Integer.parseInt(tokens[6]);
				Fleet f = new Fleet(owner, numShips, source, destination, totalTripLength, turnsRemaining);
				fleetz.add(f);
			} else {
				return 0;
			}
		}
		return 1;
	}

}
