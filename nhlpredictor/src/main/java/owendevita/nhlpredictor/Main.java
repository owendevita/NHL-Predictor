package owendevita.nhlpredictor;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    
	public static void main( String[] args ) {
       
		APIAccessor api = new APIAccessor();
		DataHandler dataHandler = new DataHandler();
		
		HashMap<String, Integer> teamIDMap = api.teamList();
	    teamIDMap.remove("seattle kraken");
	    
	    int year1 = 2000;
	    int year2 = 2001;
	    
	    for(; year2 < 2022; year2++) {
	    	
	    	int yearInt = Integer.valueOf("" + year1 + year2);
	    	
	    	if(yearInt == 20042005) {
	    		
	    		year1++;
	    		continue;
	    	}
	    	
	    	System.out.println("Starting year " + yearInt + ".");
	    	
	    	
	    	ArrayList<Integer> teamIDList = api.idList(yearInt);
	    	
	    	for(int teamID : teamIDList) {
	    		
	    		Team team = new Team(teamID, yearInt);
	    		
	    		System.out.println("Team " + teamID + " created for year " + yearInt);
	    		
	    		dataHandler.writeCSVFile(team);
	    		
	    	}
	    	
	    	year1++;
	    	
	    }
   
	}
}
