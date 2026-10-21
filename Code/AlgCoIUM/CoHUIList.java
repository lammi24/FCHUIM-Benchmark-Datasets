package AlgCoIUM;

import java.util.ArrayList;

public class CoHUIList {
	private ArrayList<CoHUI>	coHUIs;
	
	// constructor
	public CoHUIList() {
		this.coHUIs = new ArrayList<CoHUI>();
	}

	// returns number of CHUIs
	public int size() {
		return this.coHUIs.size();
	}
	
	// add a new CHUI
	public void add(CoHUI item) {
		this.coHUIs.add(item);
	}
	
	
	// returns the list of all CHUIs found
	public ArrayList<CoHUI> getCoHUIs() {
		return this.coHUIs;
	}
	
	// return the CHUI at a given index
	public CoHUI get(int i) {
		return this.coHUIs.get(i);
	}
	
	// for displaying to console or writing into file
	public String toString() {
		String	result = "";
		
		for (int i = 0; i < this.coHUIs.size(); i++) {
			CoHUI coHui = this.coHUIs.get(i);
			result += (i+1) + ": " + coHui.toString() +  "\n";
		}
		return result;
	}

}
