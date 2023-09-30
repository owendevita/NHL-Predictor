package owendevita.nhlpredictor;

import java.util.ArrayList;
import java.util.HashMap;

public class CSVCreator {
	
	private APIAccessor api = new APIAccessor();
	private DataHandler dataHandler = new DataHandler();
	private HashMap<String, Integer> teamIDMap = api.teamList();
	
	public CSVCreator() {

		teamIDMap.remove("seattle kraken");
	}
	
	public  void createOutputCSV(int year1, int year2) {
		
		System.out.println("Writing output CSV...");
		
	    for(; year2 < 2022; year2++) {
	    	
	    	int yearInt = Integer.valueOf("" + year1 + year2);
	    	
	    	if(yearInt == 20042005) {
	    		
	    		year1++;
	    		continue;
	    	}
	    	
	    	ArrayList<Integer> teamIDList = api.idList(yearInt);
	    	
	    	for(int teamID : teamIDList) {
	    		
	    		Team team = new Team(teamID, yearInt);
	    		    		
	    		dataHandler.writeOutputCSVFile(team);
	    		
	    	}
	    	
	    	year1++;
	    	
	    }
		
		
	    System.out.println("Output CSV completed.");
	    
	}
	
	public void createTestCSV(int year1, int year2) {
		
		System.out.println("Writing test CSV...");
		
		for(; year2 < 2024; year2++) {
		    	
		    	int yearInt = Integer.valueOf("" + year1 + year2);
		    	
		    	if(yearInt == 20042005) {
		    		
		    		year1++;
		    		continue;
		    	}
		    	
		    	ArrayList<Integer> teamIDList = api.idList(yearInt);
		    	
		    	for(int teamID : teamIDList) {
		    		
		    		Team team = new Team(teamID, yearInt);
		    		
		    		dataHandler.writeTestCSVFile(team);
		    		
		    	}
		    	
		    	year1++;
		    	
		    }
			
			
		    System.out.println("Test CSV completed.");
		
		
	}
	
}
