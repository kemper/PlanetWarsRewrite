
public class Reservation {
	Attack attack;
	int turn;
	int turnPlaced;
	boolean processed = false;
	int reservationGroupId;

	public Reservation(Attack attack, int turn) {
		this.attack = attack;
		this.turn = turn;
	}

	public Reservation(Attack attack, int turnPlaced, int stepInTheFuture) {
		this.attack = attack;
		this.turnPlaced = turnPlaced;
		this.turn = turnPlaced + stepInTheFuture;
	}

	public Reservation(int reservationGroupId, Attack attack, int turnPlaced, int stepInTheFuture) {
		this(attack, turnPlaced, stepInTheFuture);
		this.reservationGroupId = reservationGroupId;
	}
	
	@Override
	public String toString() {
		return "Reservation [attack=" + attack + ", processed=" + processed + ", reservationGroupId="
				+ reservationGroupId + ", turn=" + turn + ", turnPlaced=" + turnPlaced + "]";
	}
	
}
