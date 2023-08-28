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
    
    Team team1 = new Team(26);
    
    System.out.println(team1.getTeamRecord());
    System.out.println(team1.getStreak());
    System.out.println(team1.averageSavePctg());
	
	}
}
