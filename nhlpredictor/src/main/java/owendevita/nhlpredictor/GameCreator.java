package owendevita.nhlpredictor;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class GameCreator {
	private APIAccessor api = new APIAccessor();
	
	public void generateSchedule() {
		
		URL scheduleURL = api.urlCreator("https://statsapi.web.nhl.com/api/v1/schedule?season=20102011,20112012,20122013,20132014,20142015,20152016,20162017,"
				+ "20172018,20182019,20192020,20202021,20212022,20222023&teamId=1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,52,53,54,55&gameType=R");
		HttpURLConnection scheduleUrlConn = api.httpConnectionCreator(scheduleURL);
		StringBuffer scheduleString = api.apiReader(scheduleUrlConn);
		JSONObject scheduleJson = new JSONObject(scheduleString.toString());
		JSONArray dateArray = scheduleJson.getJSONArray("dates");
		
		int totalDates = dateArray.length();
		
		int totalGames = 0;
		
		for(int i = 0; i < totalDates; i++) {
		
			JSONObject currentDate = dateArray.getJSONObject(i);
			
			JSONArray gameArray = currentDate.getJSONArray("games");
			
			totalGames += gameArray.length();
			
		}
		
		
		System.out.println(totalGames);
		
	}
}
