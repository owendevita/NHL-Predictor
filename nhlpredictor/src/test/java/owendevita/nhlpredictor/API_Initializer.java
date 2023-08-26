package owendevita.nhlpredictor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.*;

public class API_Initializer {
	
	private URL teamsURL = null;
	
	public API_Initializer() {
		
		try {
			
			teamsURL = new URL("https://statsapi.web.nhl.com/api/v1/teams");
		
		} catch (MalformedURLException e) {
		
			System.out.print("URL Error: ");
			e.printStackTrace();
		
		}
		
	}
	
	public HashMap<String, Integer> teamList(HashMap<String, Integer> returnMap){
		
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

                JSONObject json = new JSONObject(informationString);
               
			}
			
			
		
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
		
		
		return returnMap;
		
	}
	
	
}
