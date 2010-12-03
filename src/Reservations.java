import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reservations extends PlanetWarsList<Reservation, Reservations>{

	@Override
	Reservations build(List<Reservation> items) {
		return new Reservations(items);
	}

	@Override
	Reservations build(Reservation... items) {
		return new Reservations(items);
	}

	public Reservations(Reservation...reservations) {
		this.items = Arrays.asList(reservations);
	}

	public Reservations(List<Reservation> reservations) {
		this.items = reservations;
	}

	public int ships(Planet futurePlanet, int currentTurn) {
		int ships = 0;
		for (Reservation reservation : items) {
			if (reservation.attack.homePlanet.equals(futurePlanet) && currentTurn == reservation.turn) {
				ships += reservation.attack.ships;
			}
		}
		return ships;
	}

	public void purge(int currentTurn) {
		List<Reservation> reservations = new ArrayList<Reservation>();
		for(Reservation r : items) {
			if (r.turn <= currentTurn) {
				reservations.add(r);
			}
		}
		items.removeAll(reservations);
	}

	public void cancel(Reservation reservation) {
		items.remove(reservation);
	}

}
