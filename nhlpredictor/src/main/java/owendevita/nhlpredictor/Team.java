package owendevita.nhlpredictor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.*;

public class Team {
	
	private int teamID;
	private int conferenceID;
	private int divisionID;
	public int year = 20222023;
	private JSONObject teamStats;
	private JSONObject teamPage;
	private JSONObject teamStandings;
	private TeamRecord teamRecord;
	private APIAccessor api = new APIAccessor();
	private ArrayList<ArrayList<Player>> roster = new ArrayList<>();
	
	public Team(int teamID) {
		
		this.teamID = teamID;
		generateTeamPage();
		generateTeamStats();
		generateTeamRecord();
		generateTeamStandings();
		// generateTeamRoster();
	}
	
	public Team(int teamID, int year) {
		
		this.year = year;
		this.teamID = teamID;
		generateTeamPage();
		generateTeamStats();
		generateTeamRecord();
		generateTeamStandings();
		// generateTeamRoster();
	}
	
	// internal private methods
	
	/**
	 * Access the team's page via API and generates
	 * a JSON object to hold the data, populating the
	 * teamPage member variable.
	 * 
	 * Via https://statsapi.web.nhl.com/api/v1/teams/ID?expand=team.stats
	 */
	private void generateTeamPage() {
		
		URL teamURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/teams/" + this.teamID + "?expand=team.stats&season=" + year);
		HttpURLConnection teamUrlConn = api.httpConnectionCreator(teamURL);
		StringBuffer teamString = api.apiReader(teamUrlConn);
		
        JSONObject teamJson = new JSONObject(teamString.toString());
        JSONObject informationJson = teamJson.getJSONArray("teams").getJSONObject(0);
		this.teamPage = informationJson;
		
		this.conferenceID = informationJson.getJSONObject("conference").getInt("id");
		this.divisionID = informationJson.getJSONObject("division").getInt("id");
		
	}
	
	/**
	 * Specifically accesses the teamPage's "teamStats" section
	 * to generate a JSON object to hold that data.
	 * 
	 */
	private void generateTeamStats() {

        JSONObject statArray = teamPage.getJSONArray("teamStats").getJSONObject(0);
        JSONObject teamStats = statArray.getJSONArray("splits").getJSONObject(0).getJSONObject("stat");
		
        this.teamStats = teamStats;
        
	}
	
	/**
	 * Utilizes the teamStats information to generate
	 * a new TeamRecord and populate the teamRecord member variable
	 * 
	 */
	private void generateTeamRecord() {
		
		TeamRecord record = new TeamRecord();
		
		record.setWins(this.teamStats.getInt("wins"));
		record.setLosses(this.teamStats.getInt("losses"));
		record.setOtLosses(this.teamStats.getInt("ot"));
		
		this.teamRecord = record;
		
	}	
	
	/**
	 * Sets the teamStandings member variable to the specific JSON object pertaining
	 * to that team's standings from the standings list.
	 * 
	 *  Via https://statsapi.web.nhl.com/api/v1/standings
	 *
	 */
	private void generateTeamStandings() {
		
		URL standingsURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/standings?season=" + year);
		HttpURLConnection standingsUrlConn = api.httpConnectionCreator(standingsURL);		
		StringBuffer standingsString = api.apiReader(standingsUrlConn);
		
        JSONObject standingsJson = new JSONObject(standingsString.toString());
        JSONArray standingsJsonArray = standingsJson.getJSONArray("records");
			    
            
        for (int i = 0; i < standingsJsonArray.length(); i++) {
        	
        	JSONObject standingsObject = (JSONObject) standingsJsonArray.get(i);
        	
        	// reduce time spent looping over teams by only searching in our team's conference and division
    		if(standingsObject.getJSONObject("conference").getInt("id") == conferenceID &&
    			standingsObject.getJSONObject("division").getInt("id") == divisionID) {
    			
    			JSONArray teamRecords = standingsObject.getJSONArray("teamRecords");
    			
    			// loop through every team in the division
    			for(int j = 0; j < teamRecords.length(); j++) {
    				
    				JSONObject teamRecordObject = teamRecords.getJSONObject(j);
    				
    				if (teamRecordObject.getJSONObject("team").getInt("id") == teamID) {
    					
    					this.teamStandings = teamRecordObject;
    					
    					
    				}
    				
    			}
    			
    		}
        	
		}
		
	}
	
	
	public ArrayList<String> gameStats(int gameID, boolean home){
		
		ArrayList<String> returnList = new ArrayList<>();
		
		URL gameURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/game/" + gameID +"/boxscore");
		HttpURLConnection gameUrlConn = api.httpConnectionCreator(gameURL);		
		StringBuffer gameString = api.apiReader(gameUrlConn);
		
        JSONObject gameJson = new JSONObject(gameString.toString());
		JSONObject gameJsonObject = gameJson.getJSONObject("teams");
		
		System.out.println("Taking a look at gameID: " + gameID);
		
		String goalieID;
		String ppOpportunities;
		String ppPercentage;
		String faceOffPercentage;
		String savePercentage;
		String shotsFor;
		String shotsAgainst;
		String penaltyMinutes;
		String pkPercentage;
		
		
		JSONObject teamObject = (home) ? gameJsonObject.getJSONObject("home") : gameJsonObject.getJSONObject("away");
		JSONObject opponentObject = (home) ? gameJsonObject.getJSONObject("away") : gameJsonObject.getJSONObject("home");
		
		JSONObject statObject = teamObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
		JSONObject opponentStatObject = opponentObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
		
		ppOpportunities = Integer.toString(statObject.getInt("powerPlayOpportunities"));
		ppPercentage = statObject.getString("powerPlayPercentage");
		faceOffPercentage = statObject.getString("faceOffWinPercentage");
		shotsFor = Integer.toString(statObject.getInt("shots"));
		penaltyMinutes = Integer.toString(statObject.getInt("pim"));
		
		double opponentPPPercentage = Double.valueOf(opponentStatObject.getString("powerPlayPercentage"));
		pkPercentage = Double.toString(100 - opponentPPPercentage);
		
		JSONArray goalies = teamObject.getJSONArray("goalies");
		JSONObject players = teamObject.getJSONObject("players");
		
		if (goalies.length() > 1) {
			
			goalieID = Integer.toString(goalies.getInt(goalies.length() - 1));
			
			double combinedSavePercentage = 0;
			int combinedShotsAgainst = 0;
			
			for(int i = 0; i < goalies.length(); i++) {
				String ID = Integer.toString(goalies.getInt(i));
				JSONObject goalieObject = players.getJSONObject("ID" + ID);
				
				
				JSONObject goalieStatObject = goalieObject.getJSONObject("stats").getJSONObject("goalieStats");
				
				double goalieSavePercentage;
				
				try {
					
					goalieSavePercentage = goalieStatObject.getDouble("savePercentage");
					
				} catch (org.json.JSONException e) {
					
					System.out.println("EXCEPTION: NO GOALIE SAVE PERCENTAGE FOUND");
					goalieSavePercentage = 100;
					
				}
				
				combinedSavePercentage += goalieSavePercentage;
				combinedShotsAgainst += goalieStatObject.getInt("shots");
				
			}
			
			combinedSavePercentage /= goalies.length();
			
			savePercentage = Double.toString(combinedSavePercentage);
			shotsAgainst = Integer.toString(combinedShotsAgainst);
			
		} else {
			
			goalieID = Integer.toString(goalies.getInt(0));
			
			JSONObject goalieObject = players.getJSONObject("ID" + goalieID);
			
			JSONObject goalieStatObject = goalieObject.getJSONObject("stats").getJSONObject("goalieStats");
			
			savePercentage = Double.toString(goalieStatObject.getDouble("savePercentage"));
			
			shotsAgainst = Integer.toString(goalieStatObject.getInt("shots"));
			
		}
		
		returnList.add(goalieID);
		returnList.add(ppOpportunities);
		returnList.add(ppPercentage);
		returnList.add(faceOffPercentage);
		returnList.add(savePercentage);
		returnList.add(shotsFor);
		returnList.add(shotsAgainst);
		returnList.add(penaltyMinutes);
		returnList.add(pkPercentage);
		
		return returnList;
		
	}
	
	
	public ArrayList<ArrayList<String>> generateTeamSchedule() {
		
		ArrayList<ArrayList<String>> returnList = new ArrayList<>();
		
		URL scheduleURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/schedule?season=" + year +"&teamId=" + teamID + "&gameType=R");
		HttpURLConnection scheduleUrlConn = api.httpConnectionCreator(scheduleURL);		
		StringBuffer scheduleString = api.apiReader(scheduleUrlConn);
		
        JSONObject scheduleJson = new JSONObject(scheduleString.toString());
        JSONArray scheduleJsonArray = scheduleJson.getJSONArray("dates");
		
        for(int i = 0; i < scheduleJsonArray.length(); i++) {
        	
        	String date;
        	String location;
        	String outcome; // 1 = win, 0 = loss
        	String opponentID;
        	String goalsAllowed;
        	String goalsFor;
        	
        	
        	boolean home = false;
        	
        	JSONObject currentGame = scheduleJsonArray.getJSONObject(i);
        	
        	date = currentGame.getString("date");

        	JSONArray gameData = currentGame.getJSONArray("games");
        	JSONObject gameDataObject = gameData.getJSONObject(0);
        	
        	JSONObject gameInfo = gameDataObject.getJSONObject("teams");
        	
        	JSONObject awayInfo = gameInfo.getJSONObject("away");
			JSONObject homeInfo = gameInfo.getJSONObject("home");
			
			int awayScore = awayInfo.getInt("score");
			int homeScore = homeInfo.getInt("score");
			
			if (homeInfo.getJSONObject("team").getInt("id") == teamID) {
				
				location = "Home";
				
				home = true;
				
				outcome = (awayScore > homeScore) ? "0" : "1";
				
				goalsAllowed = Integer.toString(awayScore);
				goalsFor = Integer.toString(homeScore);
				
				opponentID = String.valueOf(awayInfo.getJSONObject("team").getInt("id"));
				
				
			} else {
				
				location = "Away";
			
				outcome = (awayScore > homeScore) ? "1" : "0";
				
				goalsAllowed = Integer.toString(homeScore);
				goalsFor = Integer.toString(awayScore);

				
				opponentID = String.valueOf(homeInfo.getJSONObject("team").getInt("id"));
				
			}
			
			int gameID = gameDataObject.getInt("gamePk"); // to get more specific stats about the game
           
			ArrayList<String> addList = new ArrayList<>();
			addList.add(date);
			addList.add(String.valueOf(teamID));
			addList.add(location);
			addList.add(outcome);
			addList.add(opponentID);
			addList.add(goalsFor);
			addList.add(goalsAllowed);
			addList.addAll(gameStats(gameID, home));
			
	        returnList.add(addList);
        }
		
        return returnList;
        
	}
	
	
	/**
	 * Sets the roster member variable to an ArrayList of Forwards, Defenseman, and Goalie. Each
	 * player object will contain their personal stats, as well as their identifying
	 * ID number.
	 * 
	 */
	private void generateTeamRoster() {
		
		URL rosterURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/teams/" + teamID + "/roster");
		HttpURLConnection rosterUrlConn = api.httpConnectionCreator(rosterURL);		
		StringBuffer rosterString = api.apiReader(rosterUrlConn);
		
		JSONObject rosterJson = new JSONObject(rosterString.toString());
	    JSONArray rosterJsonArray = rosterJson.getJSONArray("roster");
	    
	    
	    roster.add(new ArrayList<Player>());
	    roster.add(new ArrayList<Player>());
	    roster.add(new ArrayList<Player>());
	     
	    for(int i = 0; i < rosterJsonArray.length(); i++) {
	    	 
	    	 JSONObject currentPlayer = rosterJsonArray.getJSONObject(i);
	    	 JSONObject playerInfo = currentPlayer.getJSONObject("person");
	    	 JSONObject positionInfo = currentPlayer.getJSONObject("position");
	    	 
	    	 switch(positionInfo.getString("type")) {
	    	 	
	    	 	case "Forward":
	    	 		
	    	 		Forward forward = new Forward(playerInfo.getInt("id"), playerInfo.getString("fullName"));
	    	 		
	    	 		roster.get(0).add(forward);
	    	 		
	    	 		forward.getStats();
	    	 		
	    	 		break;
	    	 	
	    	 	case "Defenseman":
	    	 		
	    	 		Defense defenseman = new Defense(playerInfo.getInt("id"), playerInfo.getString("fullName"));
	    	 		
	    	 		roster.get(1).add(defenseman);
	    	 		
	    	 		break;
	    	 		
	    	 	case "Goalie":
	    	 		
	    	 		Goalie goalie = new Goalie(playerInfo.getInt("id"), playerInfo.getString("fullName"));
	    	 		
	    	 		roster.get(2).add(goalie);
	    	 		
	    	 		break;
	    	 
	    	 }
	    	 	 
	     }
	    
	}
	
	
	private String generateString(Object o) {
		
		return String.valueOf(o);
	}
	
	// member variable getters / setters

	public int getTeamID() {
		
		return teamID;
	}

	public TeamRecord getTeamRecord() {
		
		return teamRecord;
	}
	
	public ArrayList<ArrayList<Player>> getRoster() {
		
		return roster;
	}
	
	// API methods
	
	/**
	 * Finds and outputs the current game streak of a team.
	 * For example, a win-streak of 2 games would return 2. A loss streak of 2 games would return -2.
	 * 
	 * @return	returns the number of games the team is on a streak for. negative numbers represent a loss streak, positive numbers represent a win streak
	 */
	public Integer getStreak() {

		JSONObject streakObject = teamStandings.getJSONObject("streak");
		
		// if the streakType is not a loss, return the streakNumber as positive, otherwise return it as negative
		return (!streakObject.getString("streakType").equals("losses")) ? streakObject.getInt("streakNumber") : streakObject.getInt("streakNumber") * (-1);
		
	}
	
	
	public ArrayList<ArrayList<String>> csvData() {
		
		ArrayList<ArrayList<String>> returnList = generateTeamSchedule();
		
		return returnList;
		
		
	}
	
}
