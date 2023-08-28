package owendevita.nhlpredictor;

import java.net.HttpURLConnection;
import java.net.URL;

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
	
	public Team(int teamID) {
		
		this.teamID = teamID;
		generateTeamPage();
		generateTeamStats();
		generateTeamRecord();
		generateTeamStandings();
	}
	
	// internal private methods
	
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
	
	
	private void generateTeamStats() {

        JSONObject statArray = teamPage.getJSONArray("teamStats").getJSONObject(0);
        JSONObject teamStats = statArray.getJSONArray("splits").getJSONObject(0).getJSONObject("stat");
		
        this.teamStats = teamStats;
        
	}
	
	private void generateTeamRecord() {
		
		TeamRecord record = new TeamRecord();
		
		record.setWins(this.teamStats.getInt("wins"));
		record.setLosses(this.teamStats.getInt("losses"));
		record.setOtLosses(this.teamStats.getInt("ot"));
		
		this.teamRecord = record;
		
	}	
	
	
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
	
	
	// getters / setters

	public int getTeamID() {
		
		return teamID;
	}

	public TeamRecord getTeamRecord() {
		
		return teamRecord;
	}
	
	
	// other methods
	
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
	
	
	
}
