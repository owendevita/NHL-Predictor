package owendevita.nhlpredictor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

import org.json.*;

public class API_Initializer {
	
	
	
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
	 * Returns a map of NHL Team names to their associated API ID.
	 * Used for accessing any information about the team through the API.
	 * 
	 * @return a map of team names to their associated API ID
	 */
	public HashMap<String, Integer> teamList(){
		
		HashMap<String, Integer> returnMap = new HashMap<>();
		URL teamsURL = urlCreator("https://statsapi.web.nhl.com/api/v1/teams");
		
		try {
		
			HttpURLConnection urlConn = (HttpURLConnection) teamsURL.openConnection();
			urlConn.setRequestMethod("GET");
			urlConn.connect();
			
			if(urlConn.getResponseCode() != 200) {
				
				throw new RuntimeException("Response code: " + urlConn.getResponseCode());
				
			} else {
				
				StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(teamsURL.openStream());

                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                
                scanner.close();

                
                
                JSONObject json = new JSONObject(informationString.toString());
  
                
                JSONArray jsonArray = json.getJSONArray("teams");
                
                
                for (int i = 0; i < jsonArray.length(); i++) {
                	
                	JSONObject teamObject = (JSONObject) jsonArray.get(i);
                	
                	if (teamObject.getBoolean("active")) {
                		
                		System.out.println(teamObject.getString("name") + " : " + teamObject.getInt("id")
                		+ " added to map.");
                		
                		returnMap.put(teamObject.getString("name"), teamObject.getInt("id"));
                		
                	}
                
                	
				}
                
               
			}
			
			
		
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
		
		
		return returnMap;
		
	}
	
	
	
	
}
