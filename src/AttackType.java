public enum AttackType {
	Mine {
		public Planets planets(Planets planets) {
			return planets.myPlanets();
		}
	},
	Enemy {
		public Planets planets(Planets planets) {
			return planets.enemyPlanets();
		}
	},
	All {
		public Planets planets(Planets planets) {
			return planets;
		}
	};
	abstract public Planets planets(Planets planets);
};
