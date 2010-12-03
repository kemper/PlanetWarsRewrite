
public class Fleet {
	final int owner;
	final int numShips;
	final int sourcePlanet;
	final int destinationPlanet;
	final int totalTripLength;
	final int turnsRemaining;

	public Fleet(int owner, int numShips, int sourcePlanet,
			int destinationPlanet, int totalTripLength, int turnsRemaining) {
		this.owner = owner;
		this.numShips = numShips;
		this.sourcePlanet = sourcePlanet;
		this.destinationPlanet = destinationPlanet;
		this.totalTripLength = totalTripLength;
		this.turnsRemaining = turnsRemaining;
	}


	public Fleet(int owner, int numShips) {
		this.owner = owner;
		this.numShips = numShips;
		this.sourcePlanet = -1;
		this.destinationPlanet = -1;
		this.totalTripLength = -1;
		this.turnsRemaining = -1;
	}

	public Fleet(Attack attack) {
		this(attack.homePlanet.owner, attack.ships, attack.homePlanet.id, attack.target.id, attack.homePlanet.distance(attack.target), attack.homePlanet.distance(attack.target));
	}

	public String toString() {
		return "Fleet [owner=" + owner 
				+ ", numShips=" + numShips 
				+ ", sourcePlanet=" + sourcePlanet 
				+ ", destinationPlanet=" + destinationPlanet
				+ ", totalTripLength=" + totalTripLength
				+ ", turnsRemaining=" + turnsRemaining + "]"; 
	}

	public Fleet move(int distance) {
		return new Fleet(owner, numShips, sourcePlanet,
				destinationPlanet, totalTripLength, turnsRemaining - distance);
	}

	public Fleet move() {
		return move(1);
	}
}
