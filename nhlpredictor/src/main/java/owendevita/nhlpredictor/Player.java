package owendevita.nhlpredictor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Player {
	
	private int playerID;
	private String playerName;
	private APIAccessor api = new APIAccessor();
	
	public Player(int playerID, String playerName) {
		
		this.playerID = playerID;
		this.playerName = playerName;
		
	}
	
	// Map from stat name to stat value
	public abstract HashMap<String, Double> getStats();

	public JSONObject getStatJson(int playerID) {
		
		URL statURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/people/" + playerID + "/stats?stats=statsSingleSeason&season=20222023");
		HttpURLConnection statUrlConn = api.httpConnectionCreator(statURL);
		StringBuffer statString = api.apiReader(statUrlConn);
		
		
		// cant find splits, use printing to figure it out
        JSONObject teamJson = new JSONObject(statString.toString());
        JSONArray splitsArray = teamJson.getJSONArray("stats").getJSONObject(0).getJSONArray("splits");
        JSONObject informationJson = splitsArray.optJSONObject(0).getJSONObject("stat");
        
        return informationJson;
		
	}
	
	
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
