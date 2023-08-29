package owendevita.nhlpredictor;

import java.util.HashMap;

public class Main {
    
	public static void main( String[] args ) {
       
	APIAccessor api = new APIAccessor();
	
	HashMap<String, Integer> teamIDMap = api.teamList();
    
    Team team1 = new Team(26);
    
    System.out.println(team1.getTeamRecord());
    System.out.println(team1.getStreak());
    System.out.println(team1.averageSavePctg());
    System.out.println(team1.goalsPerGameRate());
    System.out.println(team1.faceoffWinPercentage());

	
	}
}
