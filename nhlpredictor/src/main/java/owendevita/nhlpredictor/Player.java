package owendevita.nhlpredictor;

import java.util.HashMap;

public abstract class Player {
	
	private int playerID;
	private String playerName;
	
	public Player(int playerID, String playerName) {
		
		this.playerID = playerID;
		this.playerName = playerName;
		
	}
	
	// Map from stat name to stat value
	public abstract HashMap<String, Double> getStats();

	
	public int getPlayerID() {
		return playerID;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	@Override
	public String toString() {
		
		
		return "ID " + playerID + " - " + playerName;
	}
	
	
}
