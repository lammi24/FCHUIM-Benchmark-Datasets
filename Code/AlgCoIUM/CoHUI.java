package AlgCoIUM;

import java.util.List;

public class CoHUI {

	public int[] itemset; 
	public double utility = 0; 
	double cor=0;
	
	
	
	public CoHUI(int[] itemset, double utility, double cor) {
		super();
		this.itemset = itemset;
		this.utility = utility;
		this.cor = cor;
	}

	public double getCor() {
		return cor;
	}

	public void setCor(double cor) {
		this.cor = cor;
	}

	/**
	 * Get the items as array
	 * @return the items
	 */
	public int[] getItems() {
		return itemset;
	}
	
	/**
	 * Constructor
	 */
	public CoHUI(){
		itemset = new int[]{};
	}
	
	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 */
	public CoHUI(int item){
		itemset = new int[]{item};
	}

	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 */
	public CoHUI(int [] items){
		this.itemset = items;
	}
	
	/**
	 * Constructor 
	 * @param items a list of Integer representing items in the itemset
	 * @param utility the utility of the itemset
	 */
	public CoHUI(List<Integer> itemset, double utility, double cor){
		this.itemset = new int[itemset.size()];
	    int i = 0;
	    for (Integer item : itemset) { 
	    	this.itemset[i++] = item.intValue();
	    }
	    this.utility = utility;
	    this.cor=cor;
	}
	
	/**
	 * Constructor
	 * @param itemset
	 * @param utility
	 */
	public CoHUI(int[] itemset, double utility){
		this.itemset = itemset;
	    this.utility = utility;
	    this.cor=0;
	}
	
	/**
	 * Get the utility of this itemset
	 */
	public double getUtility(){
		return utility;
	}
	
	/**
	 * Get the size of this itemset 
	 */
	public int size() {
		return itemset.length;
	}

	/**
	 * Get the item at a given position in this itemset
	 */
	public Integer get(int position) {
		return itemset[position];
	}

	/**
	 * Set the utility of this itemset
	 * @param utility the utility
	 */
	public void setUtility(double utility) {
		this.utility = utility;
	}

	/**
	 * Make a copy of this itemset but exclude a given item
	 * @param itemToRemove the given item
	 * @return the copy
	 */
	public CoHUI cloneItemSetMinusOneItem(Integer itemToRemove) {
		// create the new itemset
		int[] newItemset = new int[itemset.length -1];
		int i=0;
		// for each item in this itemset
		for(int j =0; j < itemset.length; j++){
			// copy the item except if it is the item that should be excluded
			if(itemset[j] != itemToRemove){
				newItemset[i++] = itemset[j];
			}
		}
		return new CoHUI(newItemset); // return the copy
	}
	
	/**
	 * Get a string representatino of this transaction
	 * @return a string
	 */
	public String toString(){
		// use a string buffer for more efficiency
		StringBuffer r = new StringBuffer ();
		// for each item, append it to the stringbuffer
		for(int i=0; i< size(); i++){
			r.append(get(i));
			r.append(' ');
		}
		r.deleteCharAt(r.length()-1);
		r.append("#UTIL: "); r.append((int)this.utility);
		r.append("#COR: "); r.append(this.cor);
		
		return r.toString(); // return the tring
	}

}
