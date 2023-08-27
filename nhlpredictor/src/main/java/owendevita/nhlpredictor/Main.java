package owendevita.nhlpredictor;

import java.util.HashMap;

public class Main {
    
	public static void main( String[] args ) {
        
	/*
	 * Access API, set up JSON reading and format data to strings for initial testing.
	 * 
	 */
		
	APIAccessor api = new APIAccessor();
	
	HashMap<String, Integer> teamMap = api.teamList();
		
    int streak = api.getStreak(26);
    
    System.out.println(streak);
	
	}
}
