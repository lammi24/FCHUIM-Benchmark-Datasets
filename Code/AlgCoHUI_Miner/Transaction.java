package AlgCoHUI_Miner;

public class Transaction {
	int items[];
	int utilities[];
	int tu;
	int tid;
	
	public static int[] tempItems = new int[4000];
	public static int[] tempUtilities = new int[4000];
	
	public Transaction(int[] items, int[] utilities, int tu) {
		super();
		this.items = items;
		this.utilities = utilities;
		this.tu=tu;
	}

	public int[] getItems() {
		return items;
	}

	public void setItems(int[] items) {
		this.items = items;
	}

	public int[] getUtilities() {
		return utilities;
	}

	public void setUtilities(int[] utilities) {
		this.utilities = utilities;
	}

	public int getTu() {
		return tu;
	}

	public void setTu(int tu) {
		this.tu = tu;
	}

	@Override
	public String toString() {
		StringBuffer strBuff=new StringBuffer();
		int n=items.length;
		for(int i=0;i<n;i++)
 			strBuff.append(items[i] + "["+utilities[i]+"] ");
		strBuff.append(":" + tu + "\n");
		return strBuff.toString(); 
	}
	
	public void removeUnpromisingItems(int[] oldNamesToNewNames) {
    	int i = 0;
    	for(int j=0; j< items.length;j++) {
    		int item = items[j];
    		
    		// Convert from old name to new name
    		int newName = oldNamesToNewNames[item];
    		
    		// if the item is promising (it has a new name)
    		if(newName != 0) {
    			// copy the item and its utility
    			tempItems[i] = newName;
    			tempUtilities[i] = utilities[j];
    			i++;
    		}else{
    			// else subtract the utility of the item
    			tu -= utilities[j];
    		}
    	}
    	// copy the buffer of items back into the original array
    	this.items = new int[i];
    	System.arraycopy(tempItems, 0, this.items, 0, i);
    	
    	// copy the buffer of utilities back into the original array
    	this.utilities = new int[i];
    	System.arraycopy(tempUtilities, 0, this.utilities, 0, i);
    	
    	// Sort by increasing TWU values
    	insertionSort(this.items, this.utilities);
	}
	
	public static void insertionSort(int [] items,  int[] utitilies){
		for(int j=1; j< items.length; j++){
			int itemJ = items[j];
			int utilityJ = utitilies[j];
			int i = j - 1;
			for(; i>=0 && (items[i]  > itemJ); i--){
				items[i+1] = items[i];
				utitilies[i+1] = utitilies[i];
			}
			items[i+1] = itemJ;
			utitilies[i+1] = utilityJ;
		}
	}
	
	public String itemToString()
	{
		StringBuffer str=new StringBuffer();
		for(int i:items)
			str.append(i +" ");
		return str.toString();
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}
}
