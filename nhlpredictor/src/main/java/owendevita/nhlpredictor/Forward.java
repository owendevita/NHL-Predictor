package owendevita.nhlpredictor;

import java.util.HashMap;

public class Forward extends Player {

	
	public Forward(int playerID, String playerName) {
		
		super(playerID, playerName);
		
	}

	@Override
	public HashMap<String, Double> getStats() {
		
		
		//sometimes null if player doesnt have stats yet, handle this
		System.out.println(super.getStatJson(super.getPlayerID()).getInt("goals"));
		
		
		return null;
	}

}
