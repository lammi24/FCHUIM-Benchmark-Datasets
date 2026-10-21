package AlgCoHUI_Miner;

import java.util.List;


public class CoHUI {
	
	public int[] itemset; 
	public long utility = 0; 
	public int support=0;
	public float cor;
	
	public void xuat()
	{
		System.out.print("CoHui: ");
		for(int i:itemset)
			System.out.print(i + " ");
		System.out.println("Sup: " + support + " utility: " + utility + " cor: " + cor);
		
	}
	
	
	public int[] getItems() {
		return itemset;
	}
	

	public CoHUI(){
		itemset = new int[]{};
	}
	
	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 * Khởi tao một itemset mới chưa một item cho trước
	 */
	public CoHUI(int item){
		itemset = new int[]{item};
	}

	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 * Tạo một itemset chưa một mảng các item
	 */
	public CoHUI(int [] items){
		this.itemset = items;
	}
	
	/**
	 * Constructor 
	 * @param items a list of Integer representing items in the itemset
	 * @param utility the utility of the itemset
	 */
	public CoHUI(List<Integer> itemset, long utility){
		this.itemset = new int[itemset.size()];
	    int i = 0;
	    for (Integer item : itemset) { 
	    	this.itemset[i++] = item.intValue();
	    }
	    this.utility = utility;
	}
	
	/**
	 * Constructor
	 * @param itemset
	 * @param utility
	 */
	public CoHUI(int[] itemset, long utility){
		this.itemset = itemset;
	    this.utility = utility;
	}
	
	public CoHUI(int[] itemset, long utility, int support){
		this.itemset = itemset;
	    this.utility = utility;
	    this.support = support;
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
	public void setUtility(long utility) {
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
		r.append(":" + support);
		r.append(":" + cor);
		return r.toString(); // return the tring
	}

	public int[] getItemset() {
		return itemset;
	}

	public void setItemset(int[] itemset) {
		this.itemset = itemset;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}
	

	public float getCor() {
		return cor;
	}

	public void setCor(float cor) {
		this.cor = cor;
	}

}
