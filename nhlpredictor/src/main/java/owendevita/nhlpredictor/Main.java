package owendevita.nhlpredictor;

import java.util.HashMap;

public class Main {
    
	public static void main( String[] args ) {
        
	/*
	 * Access API, set up JSON reading and format data to strings for initial testing.
	 * 
	 */
		
	API_Initializer api = new API_Initializer();
	
	HashMap<String, Integer> teamMap = api.teamList();
		
    
	}
}
