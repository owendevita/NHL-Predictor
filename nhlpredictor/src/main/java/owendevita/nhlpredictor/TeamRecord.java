package owendevita.nhlpredictor;

public class TeamRecord {
	
	private int wins;
	private int losses;
	private int otLosses;

	
	public int getWins() {
		
		return wins;
	}
	
	public void setWins(int wins) {
		
		this.wins = wins;
	}
	
	public int getLosses() {
		
		return losses;
	}
	
	public void setLosses(int losses) {
		
		this.losses = losses;
	}

	public int getOtLosses() {
		
		return otLosses;
	}

	public void setOtLosses(int otLosses) {
		
		this.otLosses = otLosses;
	}
	
	@Override
	public String toString() {
	
		return "" + wins + "-" + losses + "-" + otLosses;	
	}
	
}
