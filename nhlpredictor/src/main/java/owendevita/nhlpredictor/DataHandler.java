package owendevita.nhlpredictor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVWriter;

public class DataHandler {
	
	CSVWriter writer;
	
	public DataHandler() {
		
		File file = new File("output.csv");
		FileWriter outputFile;
		
		try {
			
			outputFile = new FileWriter(file);
			writer = new CSVWriter(outputFile);
			
		} catch (IOException e) {
			System.out.println("Unable to write to file.");
			e.printStackTrace();
		
		}
		
		String[] header = {"Date", "Location", "Outcome", "OpponentID", "Point Percentage", "Shooting Percentage", "Shots Allowed / Game",
				"Shots / Game", "Faceoff Win Percentage", "Goals Against / Game", "Goals / Game", "Save Percentage"};
		
		writer.writeNext(header);
		
		System.out.println("Wrote header.");
		
	}
	
	public void writeCSVFile(Team team) {
		
		ArrayList<ArrayList<String>> infoList = team.csvData();
		
		
		for (ArrayList<String> list : infoList) {
			
			String[] info = new String[infoList.size()];
			info = list.toArray(info);
			
			writer.writeNext(info);
		
		}
			

		System.out.println("Wrote team " + team.getTeamID() + " year " + team.year + ".");
		
	}
	
	
	
	
	
}
