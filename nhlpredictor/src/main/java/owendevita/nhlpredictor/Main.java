package owendevita.nhlpredictor;

public class Main {
    
	public static void main( String[] args ) {
       
//		CSVCreator csvCreator = new CSVCreator();
//		
//		csvCreator.createOutputCSV(2010, 2011);
//		csvCreator.createTestCSV(2021, 2022);
		
		GameCreator creator = new GameCreator();
		
		creator.generateSchedule();
		
	
	}
}
