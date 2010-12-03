

public class Planet {
	int id;
	int owner;
	int numShips;
	final int growthRate;
	final double x, y;
	private int stepsIntoFuture;

	protected Planet(Planet _p) {
		id = _p.id;
		owner = _p.owner;
		numShips = _p.numShips;
		growthRate = _p.growthRate;
		x = _p.x;
		y = _p.y;
	}
	
	public Planet(int planetID, int owner, int numShips, int growthRate,
			double x, double y) {
		this.id = planetID;
		this.owner = owner;
		this.numShips = numShips;
		this.growthRate = growthRate;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Planet other = (Planet) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public String toString() {
		return "Planet " + id + ", Size: " + numShips + " (x = " + x + ", y = " + y + "), growthRate: " + growthRate + ", owner: " + ownerName();  
	}
	
	private String ownerName() {
		if (isMine()) {
			return "PLAYER";
		} else if (isEnemy()){
			return "ENEMY";
		}
		return "NEUTRAL";
	}

	public boolean isEnemy() {
		return owner == PlanetWars.ENEMY_ID;
	}
	
	public boolean isMine() {
		return owner == PlanetWars.PLAYER_ID;
	}
	
	public boolean isNeutral() {
		return owner == PlanetWars.NEUTRAL_ID;
	}
	
	int distance(Planet other) {
		double dx = x - other.x;
		double dy = y - other.y;
		return (int) Math.ceil(Math.sqrt(dx * dx + dy * dy));
	}
	
	protected Planet grow() {
		return grow(1);
	}
	
	protected void handleAttack(Fleet f) {
		if (f.owner != owner) {
			if (f.numShips > this.numShips) {
				this.numShips = f.numShips - this.numShips;
				this.owner = f.owner;
			} else {
				this.numShips -= f.numShips;
			}
		} else {
			this.numShips += f.numShips;
		}
	}
	
	protected void growUnlessNeutral(int turnsRemaining) {
		if (owner != PlanetWars.NEUTRAL_ID) {
			this.numShips += this.growthRate * turnsRemaining;
		}
	}
	
	public Planet move() {
		return new Planet(this);
	}

	public Planet receive(Fleet fleet) {
		if (fleet.owner != owner) {
			int ships = numShips - fleet.numShips;
			if (fleet.numShips > numShips) {
				return new Planet(id, fleet.owner, ships*-1, growthRate, x, y);
			} else {
				return new Planet(id, owner, ships, growthRate, x, y);
			}
		}
		return new Planet(id, owner, numShips + fleet.numShips, growthRate, x, y);
	}

	public Planet removeShips(int ships) {
		return new Planet(id, owner, numShips - ships, growthRate, x, y);
	}

	public Planet grow(int numberOfTimes) {
		int ships = numShips;
		if (owner != PlanetWars.NEUTRAL_ID) {
			ships += this.growthRate * numberOfTimes;
		}
		return new Planet(id, owner, ships, growthRate, x, y);
	}

	public Fleet attack(Planet planet) {
		return new Fleet(owner, numShips, id, planet.id, distance(planet), distance(planet));
	}
	
}
