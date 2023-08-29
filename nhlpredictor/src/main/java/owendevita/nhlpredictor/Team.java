package owendevita.nhlpredictor;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.*;

public class Team {
	
	private int teamID;
	private int conferenceID;
	private int divisionID;
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
		generateTeamRoster();
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
		
		URL teamURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/teams/" + this.teamID + "?expand=team.stats");
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
		
		URL standingsURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/standings");
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
	
	/**
	 * Outputs the average save percentage.
	 *
	 * @return	a double containing the average save percentage of the team
	 */
	public Double averageSavePctg() {
        
        return teamStats.getDouble("savePctg");
		
	}
	
	
	public Double goalsPerGameRate() {
		
		
		return teamStats.getDouble("goalsPerGame");	
	}
	
	public Double faceoffWinPercentage() {
		
		return Double.valueOf(teamStats.getString("faceOffWinPercentage"));
		
	}
	
	public Double teamGoalsAgainstAvg() {
		
		return teamStats.getDouble("goalsAgainstPerGame");
	}
	
}
