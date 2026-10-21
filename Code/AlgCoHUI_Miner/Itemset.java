package AlgCoHUI_Miner;
import java.util.List;
public class Itemset implements Comparable<Itemset>  {

	/** the array of items **/
	public int[] itemset; 

	/**  the utility of this itemset */
	public double utility = 0; 
	
	/** the support of Itemset */
	public int support=0;
	public int tongitem=0;
	
	public float cor;
	
	/**
	 * Get the items as array
	 * @return the items
	 */
	public int[] getItems() {
		return itemset;
	}
	
	/**
	 * Constructor-- khởi tạo một itemset rỗng
	 */
	public Itemset(){
		itemset = new int[]{};
	}
	
	/**
	 * Constructor 
	 * @param item an item that should be added to the new itemset
	 * Khởi tao một itemset mới chưa một item cho trước
	 */
	public Itemset(int item){
		itemset = new int[]{item};
	}

	/**
	 * Constructor 
	 * @param items an array of items that should be added to the new itemset
	 * Tạo một itemset chưa một mảng các item
	 */
	public Itemset(int [] items){
		this.itemset = items;
	}
	
	/**
	 * Constructor 
	 * @param items a list of Integer representing items in the itemset
	 * @param utility the utility of the itemset
	 */
	public Itemset(List<Integer> itemset, double utility){
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
	public Itemset(int[] itemset, double utility){
		this.itemset = itemset;
	    this.utility = utility;
	}
	
	public Itemset(int[] itemset, double utility, int support){
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
	public void setUtility(double utility) {
		this.utility = utility;
	}

	/**
	 * Make a copy of this itemset but exclude a given item
	 * @param itemToRemove the given item
	 * @return the copy
	 */
	public Itemset cloneItemSetMinusOneItem(Integer itemToRemove) {
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
		return new Itemset(newItemset); // return the copy
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
	
	@Override
	public int compareTo(Itemset o) {
		if(this.support<o.support)
			return -1;
		if(support>o.support)
			return 1;
		
		int size=size();
		int sizeo=o.size();
		int minsize=size>sizeo?sizeo:size;
		int i=minsize-1;
		while(i>=0)
		{
			if(itemset[i]<o.itemset[i])
				return -1;
			if(itemset[i]>o.itemset[i])
				return 1;
			i--;
		}
		if(size==sizeo)return 0;
		return size<sizeo?-1:1;
	}
	
	public boolean isContains(Itemset itemset, int k)
	{
		if(this.support!=itemset.support)
			return false;
		int n=size();
		if(n<=k)
			return false;
		int j=0;
		int dem=0;
		for(int i=0;i<k;i++)
		{
			while(j<n && this.itemset[j]<itemset.itemset[i])
				j++;
			if(j<n && this.itemset[j]==itemset.itemset[i])
			{
				dem++;
				j++;
			}
			if(n-j<k-dem)
				return false;
		}
		if(dem==k)
			return true;
		return false;
	}

	public int getTongitem() {
		return tongitem;
	}

	public void setTongitem(int tongitem) {
		this.tongitem = tongitem;
	}

	public float getCor() {
		return cor;
	}

	public void setCor(float cor) {
		this.cor = cor;
	}
	
}






