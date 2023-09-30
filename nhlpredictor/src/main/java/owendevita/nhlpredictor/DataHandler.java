package owendevita.nhlpredictor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVWriter;

public class DataHandler {
	
	public static int outputRows = 0;
	public static int testRows = 0;
	
	CSVWriter outputWriter;
	CSVWriter testWriter;
	String[] header;
	
	public DataHandler() {
		
		File outputFile = new File("output.csv");
		File testFile = new File("test.csv");
		FileWriter outputFileWriter;
		FileWriter testFileWriter;
		
		try {
			
			outputFileWriter = new FileWriter(outputFile);
			outputWriter = new CSVWriter(outputFileWriter);
			
			testFileWriter = new FileWriter(testFile);
			testWriter = new CSVWriter(testFileWriter);
			
		} catch (IOException e) {
			System.out.println("Unable to write to file.");
			e.printStackTrace();
		
		}
		
		header = new String[]{"Index", "Date", "Team", "Location", "Outcome", "OpponentID", "Point Percentage", "Shooting Percentage", "Shots Allowed / Game",
				"Shots / Game", "Faceoff Win Percentage", "Goals Against / Game", "Goals / Game", "Save Percentage"};
		
		outputWriter.writeNext(header);
		testWriter.writeNext(header);
		
		
	}
	
	public void writeOutputCSVFile(Team team) {
		
		
		ArrayList<ArrayList<String>> infoList = team.csvData();
		
		for (ArrayList<String> list : infoList) {
			
			list.add(0, Integer.toString(outputRows));
			
			String[] info = new String[list.size()];
			info = list.toArray(info);
		
			outputWriter.writeNext(info);
			
			outputRows++;
		
		}
		
	}
	
	public void writeTestCSVFile(Team team) {
		
		ArrayList<ArrayList<String>> infoList = team.csvData();
		
		for (ArrayList<String> list : infoList) {
			
			list.add(0, Integer.toString(testRows));
			
			String[] info = new String[list.size()];
			info = list.toArray(info);
			
			testWriter.writeNext(info);
			
			testRows++;
		
		}
		
	}
	
	
}
