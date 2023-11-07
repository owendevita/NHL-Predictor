package owendevita.nhlpredictor;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class DataCreator {
	
	private APIAccessor api = new APIAccessor();
	
	public LinkedList<String[]> generateSchedule() {
		
		int rowNum = 0;
		
		LinkedList<String[]> returnList = new LinkedList<>();
		
		URL scheduleURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/schedule?season=20102011,20112012,20122013,20132014,20142015,20152016,20162017,"
				+ "20172018,20182019,20192020,20202021,20212022,20222023&teamId=1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,52,53,54,55&gameType=R");
		
		System.out.println("Global schedule accessed.");
		
		HttpURLConnection scheduleUrlConn = api.httpConnectionCreator(scheduleURL);
		StringBuffer scheduleString = api.apiReader(scheduleUrlConn);
		JSONObject scheduleJson = new JSONObject(scheduleString.toString());
		JSONArray dateArray = scheduleJson.getJSONArray("dates");
		
		int totalDates = dateArray.length();
		
		for(int i = 0; i < totalDates; i++) {
			
			
			JSONObject currentDate = dateArray.getJSONObject(i);
			
			String dateString = currentDate.getString("date");
			
			JSONArray gameArray = currentDate.getJSONArray("games");
			
			int numGames = gameArray.length();
			
			for (int j = 0; j < numGames; j++) {
				
				// compiling data
				JSONObject gameDataObject = gameArray.getJSONObject(j);
				JSONObject gameInfo = gameDataObject.getJSONObject("teams");
				
	        	JSONObject awayInfo = gameInfo.getJSONObject("away");
				JSONObject homeInfo = gameInfo.getJSONObject("home");
				
				int awayScore = awayInfo.getInt("score");
				int homeScore = homeInfo.getInt("score");
				String homeOutcome = (homeScore > awayScore) ? "Win" : "Loss";
				String awayOutcome = (awayScore > homeScore) ? "Win" : "Loss";
				
				String homeOpponentID = String.valueOf(awayInfo.getJSONObject("team").getInt("id"));
				String awayOpponentID = String.valueOf(homeInfo.getJSONObject("team").getInt("id"));
				
				
				String homeGoals = Integer.toString(homeScore);
				String awayGoals = Integer.toString(awayScore);
				
				
				int gameID = gameDataObject.getInt("gamePk");
				
				URL gameURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/game/" + gameID +"/boxscore");
				HttpURLConnection gameUrlConn = api.httpConnectionCreator(gameURL);		
				StringBuffer gameString = api.apiReader(gameUrlConn);
				
		        JSONObject gameJson = new JSONObject(gameString.toString());
				JSONObject gameJsonObject = gameJson.getJSONObject("teams");
				
				System.out.println("Taking a look at gameID: " + gameID);
				
//				String goalieID;
//				String ppOpportunities;
//				String ppPercentage;
//				String faceOffPercentage;
//				String savePercentage;
//				String shotsFor;
//				String shotsAgainst;
//				String penaltyMinutes;
//				String pkPercentage;
				
				
				JSONObject homeObject = gameJsonObject.getJSONObject("home");
				JSONObject awayObject = gameJsonObject.getJSONObject("away");
				
				JSONObject homeStatObject = homeObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
				JSONObject awayStatObject = awayObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
				
				String homePPOpportunities = Integer.toString(homeStatObject.getInt("powerPlayOpportunities"));
				String awayPPOpportunities = Integer.toString(awayStatObject.getInt("powerPlayOpportunities"));
				
				String homePPPercentage = homeStatObject.getString("powerPlayPercentage");
				String awayPPPercentage = awayStatObject.getString("powerPlayPercentage");
				
				String homePKPercentage = Double.toString(100.0 - Double.valueOf(awayPPPercentage));
				String awayPKPercentage = Double.toString(100.0 - Double.valueOf(homePPPercentage));
				
				String homeFaceOffPercentage = homeStatObject.getString("faceOffWinPercentage");
				String awayFaceOffPercentage = awayStatObject.getString("faceOffWinPercentage");
				
				int intHomeShots = homeStatObject.getInt("shots");
				int intAwayShots = awayStatObject.getInt("shots");
				
				String homeShotsFor = Integer.toString(intHomeShots);
				String awayShotsFor = Integer.toString(intAwayShots);
				
				String homePenaltyMinutes = Integer.toString(homeStatObject.getInt("pim"));
				String awayPenaltyMinutes = Integer.toString(awayStatObject.getInt("pim"));
				
				int homeSaves = intAwayShots - awayScore;
				int awaySaves = intHomeShots - homeScore;
				
				double homeSavePctg = (double) homeSaves / intAwayShots;
				double awaySavePctg = (double) awaySaves / intHomeShots;
				
				JSONArray homeGoalies = homeObject.getJSONArray("goalies");
				JSONArray awayGoalies = awayObject.getJSONArray("goalies");
				
				String homeGoalieID = (homeGoalies.length() > 1) ? Integer.toString(homeGoalies.getInt(homeGoalies.length() - 1)) : Integer.toString(homeGoalies.getInt(0));
				String awayGoalieID = (awayGoalies.length() > 1) ? Integer.toString(awayGoalies.getInt(awayGoalies.length() - 1)) : Integer.toString(awayGoalies.getInt(0));
				
				String[] homeData = new String[17];
				String[] awayData = new String[17];
				
				// setting data
				
				/*
				 Array Index Key:
					0 - Index
					1 - Date
					2 - Team
					3 - Location
					4 - Outcome
					5 - OpponentID
					6 - Goals For
					7 - Goals Against
					8 - Goalie ID
					9 - PP Opportunities
					10 - PP Percentage
					11 - Face Off Percentage
					12 - Save Percentage
					13 - Shots For
					14 - Shots Against
					15 - PIM
					16 - PK Percentage
				 */
				
				homeData[0] = Integer.toString(rowNum);
				rowNum++;
				
				awayData[0] = Integer.toString(rowNum);
				rowNum++;
				
				// date is the same for both teams
				homeData[1] = awayData[1] = dateString;
				
				// one team's ID is equal to it's opponents "opponentID"
				homeData[2] = awayData[5] = awayOpponentID;
				awayData[2] = homeData[5] = homeOpponentID;
				
				homeData[3] = "Home";
				awayData[3] = "Away";
				
				homeData[4] = homeOutcome;
				awayData[4] = awayOutcome;
				
				// the amount of goals scored by one team is the number of goals scored against the other team
				homeData[6] = awayData[7] = homeGoals;
				awayData[6] = homeData[7] = awayGoals;
				
				homeData[8] = homeGoalieID;
				awayData[8] = awayGoalieID;
				
				homeData[9] = homePPOpportunities;
				awayData[9] = awayPPOpportunities;
				
				homeData[10] = homePPPercentage;
				awayData[10] = awayPPPercentage;
				
				homeData[11] = homeFaceOffPercentage;
				awayData[11] = awayFaceOffPercentage;
				
				homeData[12] = Double.toString(homeSavePctg);
				awayData[12] = Double.toString(awaySavePctg);
				
				//one team's shots against is equal to the other team's shots for
				homeData[13] = awayData[14] = homeShotsFor;
				awayData[13] = homeData[14] = awayShotsFor;
				
				homeData[15] = homePenaltyMinutes;
				awayData[15] = awayPenaltyMinutes;
				
				homeData[16] = homePKPercentage;
				awayData[16] = awayPKPercentage;
				
				returnList.add(homeData);
				returnList.add(awayData);
				
			}
			
		}
	
		return returnList;
	}
	
public LinkedList<String[]> generateTestSchedule() {
		
		int rowNum = 0;
		
		LinkedList<String[]> returnList = new LinkedList<>();
		
		URL scheduleURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/schedule?season=20232024&teamId=1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,52,53,54,55&gameType=R");
		
		System.out.println("Global test schedule accessed.");
		
		HttpURLConnection scheduleUrlConn = api.httpConnectionCreator(scheduleURL);
		StringBuffer scheduleString = api.apiReader(scheduleUrlConn);
		JSONObject scheduleJson = new JSONObject(scheduleString.toString());
		JSONArray dateArray = scheduleJson.getJSONArray("dates");
		
		int totalDates = dateArray.length();
		
		for(int i = 0; i < totalDates; i++) {
			
			
			JSONObject currentDate = dateArray.getJSONObject(i);
			
			String dateString = currentDate.getString("date");
			
			JSONArray gameArray = currentDate.getJSONArray("games");
			
			int numGames = gameArray.length();
			
			for (int j = 0; j < numGames; j++) {
				
				// compiling data
				JSONObject gameDataObject = gameArray.getJSONObject(j);
				
				String gameStatus = gameDataObject.getJSONObject("status").getString("detailedState");
				
				if (!gameStatus.equalsIgnoreCase("Final")) {
					
					break;
					
				}
				
				JSONObject gameInfo = gameDataObject.getJSONObject("teams");
				
	        	JSONObject awayInfo = gameInfo.getJSONObject("away");
				JSONObject homeInfo = gameInfo.getJSONObject("home");
				
				int awayScore = awayInfo.getInt("score");
				int homeScore = homeInfo.getInt("score");
				String homeOutcome = (homeScore > awayScore) ? "Win" : "Loss";
				String awayOutcome = (awayScore > homeScore) ? "Win" : "Loss";
				
				String homeOpponentID = String.valueOf(awayInfo.getJSONObject("team").getInt("id"));
				String awayOpponentID = String.valueOf(homeInfo.getJSONObject("team").getInt("id"));
				
				
				String homeGoals = Integer.toString(homeScore);
				String awayGoals = Integer.toString(awayScore);
				
				
				int gameID = gameDataObject.getInt("gamePk");
				
				URL gameURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/game/" + gameID +"/boxscore");
				HttpURLConnection gameUrlConn = api.httpConnectionCreator(gameURL);		
				StringBuffer gameString = api.apiReader(gameUrlConn);
				
		        JSONObject gameJson = new JSONObject(gameString.toString());
				JSONObject gameJsonObject = gameJson.getJSONObject("teams");
				
				System.out.println("Taking a look at gameID: " + gameID);
				
//				String goalieID;
//				String ppOpportunities;
//				String ppPercentage;
//				String faceOffPercentage;
//				String savePercentage;
//				String shotsFor;
//				String shotsAgainst;
//				String penaltyMinutes;
//				String pkPercentage;
				
				
				JSONObject homeObject = gameJsonObject.getJSONObject("home");
				JSONObject awayObject = gameJsonObject.getJSONObject("away");
				
				JSONObject homeStatObject = homeObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
				JSONObject awayStatObject = awayObject.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
				
				String homePPOpportunities = Integer.toString(homeStatObject.getInt("powerPlayOpportunities"));
				String awayPPOpportunities = Integer.toString(awayStatObject.getInt("powerPlayOpportunities"));
				
				String homePPPercentage = homeStatObject.getString("powerPlayPercentage");
				String awayPPPercentage = awayStatObject.getString("powerPlayPercentage");
				
				String homePKPercentage = Double.toString(100.0 - Double.valueOf(awayPPPercentage));
				String awayPKPercentage = Double.toString(100.0 - Double.valueOf(homePPPercentage));
				
				String homeFaceOffPercentage = homeStatObject.getString("faceOffWinPercentage");
				String awayFaceOffPercentage = awayStatObject.getString("faceOffWinPercentage");
				
				int intHomeShots = homeStatObject.getInt("shots");
				int intAwayShots = awayStatObject.getInt("shots");
				
				String homeShotsFor = Integer.toString(intHomeShots);
				String awayShotsFor = Integer.toString(intAwayShots);
				
				String homePenaltyMinutes = Integer.toString(homeStatObject.getInt("pim"));
				String awayPenaltyMinutes = Integer.toString(awayStatObject.getInt("pim"));
				
				int homeSaves = intAwayShots - awayScore;
				int awaySaves = intHomeShots - homeScore;
				
				double homeSavePctg = (double) homeSaves / intAwayShots;
				double awaySavePctg = (double) awaySaves / intHomeShots;
				
				JSONArray homeGoalies = homeObject.getJSONArray("goalies");
				JSONArray awayGoalies = awayObject.getJSONArray("goalies");
				
				String homeGoalieID = (homeGoalies.length() > 1) ? Integer.toString(homeGoalies.getInt(homeGoalies.length() - 1)) : Integer.toString(homeGoalies.getInt(0));
				String awayGoalieID = (awayGoalies.length() > 1) ? Integer.toString(awayGoalies.getInt(awayGoalies.length() - 1)) : Integer.toString(awayGoalies.getInt(0));
				
				String[] homeData = new String[17];
				String[] awayData = new String[17];
				
				// setting data
				
				/*
				 Array Index Key:
					0 - Index
					1 - Date
					2 - Team
					3 - Location
					4 - Outcome
					5 - OpponentID
					6 - Goals For
					7 - Goals Against
					8 - Goalie ID
					9 - PP Opportunities
					10 - PP Percentage
					11 - Face Off Percentage
					12 - Save Percentage
					13 - Shots For
					14 - Shots Against
					15 - PIM
					16 - PK Percentage
				 */
				
				homeData[0] = Integer.toString(rowNum);
				rowNum++;
				
				awayData[0] = Integer.toString(rowNum);
				rowNum++;
				
				// date is the same for both teams
				homeData[1] = awayData[1] = dateString;
				
				// one team's ID is equal to it's opponents "opponentID"
				homeData[2] = awayData[5] = awayOpponentID;
				awayData[2] = homeData[5] = homeOpponentID;
				
				homeData[3] = "Home";
				awayData[3] = "Away";
				
				homeData[4] = homeOutcome;
				awayData[4] = awayOutcome;
				
				// the amount of goals scored by one team is the number of goals scored against the other team
				homeData[6] = awayData[7] = homeGoals;
				awayData[6] = homeData[7] = awayGoals;
				
				homeData[8] = homeGoalieID;
				awayData[8] = awayGoalieID;
				
				homeData[9] = homePPOpportunities;
				awayData[9] = awayPPOpportunities;
				
				homeData[10] = homePPPercentage;
				awayData[10] = awayPPPercentage;
				
				homeData[11] = homeFaceOffPercentage;
				awayData[11] = awayFaceOffPercentage;
				
				homeData[12] = Double.toString(homeSavePctg);
				awayData[12] = Double.toString(awaySavePctg);
				
				//one team's shots against is equal to the other team's shots for
				homeData[13] = awayData[14] = homeShotsFor;
				awayData[13] = homeData[14] = awayShotsFor;
				
				homeData[15] = homePenaltyMinutes;
				awayData[15] = awayPenaltyMinutes;
				
				homeData[16] = homePKPercentage;
				awayData[16] = awayPKPercentage;
				
				returnList.add(homeData);
				returnList.add(awayData);
				
			}
			
		}
	
		return returnList;
	}
	

}
