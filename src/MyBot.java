
import java.util.List;
import static utils.Utils.*;

public class MyBot {

	public static void doTurn(PlanetWars pw) {
		if (pw.enemyPlanets.size() == 0 || pw.myPlanets.size() == 0) return;
		List<Attack> planetsToAttack = pw.attackOrders();

		for (Attack attack : planetsToAttack) {
			pw.IssueOrder(attack.homePlanet, attack.target, attack.ships);
		}
	}

	public static void main(String[] args) {
		String line = "";
		String message = "";
		int c;
		try {
			while ((c = System.in.read()) >= 0) {
				switch (c) {
				case '\n':
					if (line.equals("go")) {
						PlanetWars pw = new PlanetWars(message);
						doTurn(pw);
						pw.finishTurn();
						message = "";
					} else {
						message += line + "\n";
					}
					line = "";
					break;
				default:
					line += (char) c;
					break;
				}
			}
		} catch (Exception e) {
			log(e);
		}
	}

}
