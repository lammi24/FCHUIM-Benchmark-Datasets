package AlgCoHUI_Miner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dataset {
	List<Transaction> transactions;
	int maxItem=0;
	
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public int getMaxItem() {
		return maxItem;
	}
	public void setMaxItem(int maxItem) {
		this.maxItem = maxItem;
	}
	
	//kkhoi tao dataset rong
	public Dataset()
	{
		transactions=new ArrayList<Transaction>(); 
		maxItem=0;
	}
	//them mot tran vao co so du lieu
	public void add(Transaction tran)
	{
		transactions.add(tran);		
	}
	
	 public Dataset(String datasetPath, int maximumTransactionCount) throws IOException 
	 {
		 //maximumTransactionCount=5000000;
		 transactions = new ArrayList<Transaction>();
	        BufferedReader br = new BufferedReader(new FileReader(datasetPath));
	        String line;
	        int i=0;
	        // iterate over the lines to build the transaction
	        while((line = br.readLine()) != null) { 
				// if the line is  a comment, is  empty or is  metadata
				if (line.isEmpty() == true || line.charAt(0) == '#' 
						|| line.charAt(0) == '%' || line.charAt(0) == '@') {
					continue;
				}
				i++;
				// read the transaction
				Transaction tran=createTransaction(line);
				//them tid vao giao dịch
				tran.setTid(i);
				transactions.add(tran);
				// if the number of transaction to be read is reached, we stop
	        	if(i==maximumTransactionCount) {
	        		break;
	        	}
				
	        }
	        //****** Show the number of transactions in this dataset**************************//
	        System.out.println("Transaction count :" +  transactions.size());
	        br.close();
	 }
	 
	 
	 
	 
	 
	 private Transaction createTransaction(String line) 
	 {
		// split the line into tokens according to the ":" separator
	    	String[] split = line.split(":");
	    	
	    	// Get the transaction utility
	    	int transactionUtility = Integer.parseInt(split[1]);
	    	
	    	// Get the list of items 
	        String[] itemsString = split[0].split(" ");
	    	
	        // Get the list of item utilities
	        String[] itemsUtilitiesString = split[2].split(" ");
	    	
	        //Create array to store the items and their utilities
	        int[] items = new  int[itemsString.length];
	        int[] utilities = new  int[itemsString.length];

	        // for each item
	        for (int i = 0; i < items.length; i++) {
	        	//store the item
	        	items[i] = Integer.parseInt(itemsString[i]);
	        	
	        	// store its utility in that transaction
	        	utilities[i] = Integer.parseInt(itemsUtilitiesString[i]);
	            
	            // if the item name is larger than the largest item read from the database until now, we remember
	        	// its name
	            if(items[i] > maxItem) {
	                maxItem = items[i];
	            }
	        }

			// create the transaction object for this transaction and return it
			return new Transaction(items, utilities, transactionUtility);
	 }
	 
	@Override
	public String toString() {
		StringBuffer str=new StringBuffer("Dataset [transactions=" + transactions.size() + ", maxItem=" + maxItem + "]\n");
		for(Transaction t: transactions)
			str.append(t.toString());
		return str.toString();
	}
//		
	public long sumTu()
	{
		long t=0;
		for(Transaction tran:transactions)
			t=t+tran.getTu();
		return t;
	}
}

