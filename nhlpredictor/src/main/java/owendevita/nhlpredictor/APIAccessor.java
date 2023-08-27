package owendevita.nhlpredictor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.json.*;

public class APIAccessor {
	
	
	
	/**
	 * Creates URL objects from URLs in String form.
	 * 
	 * @param url	a string URL to be turned into an URL object
	 * @return	returns the given string URL as an URL object
	 */
	public URL urlCreator(String url) {
		
		try {
			
			URL returnURL = new URL(url);
			return returnURL;
		
		} catch (MalformedURLException e) {
		
			System.out.print("URL Error: ");
			e.printStackTrace();
			
			return null;
		
		}
		
	}
	
	
	/**
	 * Opens an HttpURLConnection to read from the API.
	 * 
	 * @param url	a URL object to be used to open an HttpURLConnection
	 * @return returns a HttpURLConnection object with request method set to get for the given URL.
	 */
	public HttpURLConnection httpConnectionCreator(URL url) {
		
		try {
			
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.connect();
			
			if(urlConn.getResponseCode() != 200) {
				
				throw new RuntimeException("Response code: " + urlConn.getResponseCode());
				
			} else {
				
				return urlConn;
				
			}
			
		} catch (IOException e) {
		
			e.printStackTrace();
			
			return null;
		
		}
		
	}
	
	/**
	 * 
	 * @param urlConn	a HttpURLConnection to be read from
	 * @return	returns a StringBuffer containing the JSON info from the API.
	 */
	public StringBuffer apiReader(HttpURLConnection urlConn) {
        
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			return response;
		
		} catch(IOException e) {
			
			return null;
			
		}
		
	}
	
	
	/**
	 * Returns a map of NHL Team names to their associated API ID.
	 * Used for accessing any information about the team through the API.
	 * 
	 * @return a map of team names to their associated API ID
	 */
	public HashMap<String, Integer> teamList(){
		
		HashMap<String, Integer> returnMap = new HashMap<>();
		URL teamsURL = urlCreator("https://statsapi.web.nhl.com/api/v1/teams");
		HttpURLConnection urlConn = httpConnectionCreator(teamsURL);
		
		StringBuffer informationString = apiReader(urlConn);

		
        JSONObject json = new JSONObject(informationString.toString());
                
        JSONArray jsonArray = json.getJSONArray("teams");
            
            
            for (int i = 0; i < jsonArray.length(); i++) {
            	
            	JSONObject teamObject = (JSONObject) jsonArray.get(i);
            	
            	if (teamObject.getBoolean("active")) {
            		
//            		System.out.println(teamObject.getString("name") + " : " + teamObject.getInt("id")
//            		+ " added to map.");
            		
            		returnMap.put(teamObject.getString("name"), teamObject.getInt("id"));
            		
            	}
            
            	
			}

		return returnMap;
		
	}
	
	
	// specific team data below
	
	/**
	 * Finds and outputs the current game streak of a team.
	 * For example, a win-streak of 2 games would return 2. A loss streak of 2 games would return -2.
	 * 
	 * @param teamID	the ID of the team that you want to get the streak for
	 * @return	returns the number of games the team is on a streak for. negative numbers represent a loss streak, positive numbers represent a win streak
	 */
	public Integer getStreak(int teamID) {
		
		// access the team's specific page to grab the conference and division ID
		// TODO: Potentially store an array list of all teams conference and division IDs for faster access?
		URL teamURL = urlCreator("https://statsapi.web.nhl.com/api/v1/teams/" + teamID);
		HttpURLConnection teamUrlConn = httpConnectionCreator(teamURL);
		StringBuffer teamString = apiReader(teamUrlConn);
		
        JSONObject teamJson = new JSONObject(teamString.toString());
        JSONObject informationJson = teamJson.getJSONArray("teams").getJSONObject(0);
        
        // get conference ID
        JSONObject conferenceJson = informationJson.getJSONObject("conference");
        int conferenceID = conferenceJson.getInt("id");
        
        // get division ID
        JSONObject divisionJson = informationJson.getJSONObject("division");
        int divisionID = divisionJson.getInt("id");
        

		// access the standings page to grab streak information
		URL standingsURL = urlCreator("https://statsapi.web.nhl.com/api/v1/standings");
		HttpURLConnection standingsUrlConn = httpConnectionCreator(standingsURL);		
		StringBuffer standingsString = apiReader(standingsUrlConn);
		
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
        					
        					JSONObject streakObject = teamRecordObject.getJSONObject("streak");
        					
        					// if the streakType is not a loss, return the streakNumber as positive, otherwise return it as negative
        					return (!streakObject.getString("streakType").equals("losses")) ? streakObject.getInt("streakNumber") : streakObject.getInt("streakNumber") * (-1);
        					
        				}
        				
        			}
        			
        			
        		}
            	
			}
		
		return null;
		
	}
	
}
