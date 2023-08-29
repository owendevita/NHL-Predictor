package owendevita.nhlpredictor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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
            		
            		returnMap.put(teamObject.getString("name"), teamObject.getInt("id"));
            		
            	}
            
            	
			}

		return returnMap;
		
	}
	
}