package AlgCoIUM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CoIUM {
	
    private CoHUIList correlatedHighUtilityItemsets;
	BufferedWriter writer = null;

	private int patternCount; 


	long startTimestamp;
	long endTimestamp;
	

	int minUtil;
	double minCor;
	

    final boolean  DEBUG = false;

	private int[] utilityBinArraySU;   
	private int[] utilityBinArrayLU; 
	private int[] Sups;
	
	/** a temporary buffer */
	private int [] temp= new int [500];	
	

	long timeIntersections;
	long timeDatabaseReduction;
	long timeIdentifyPromisingItems;
	long timeSort;
	long timeBinarySearch;
	
	int transCount;
	
	static double dbSizeOnMem;


    int[] oldNameToNewNames;
    int[] newNamesToOldNames;
   
    /** the number of new items */
    int newItemCount;
    /** size of database*/
    int dbTranlen = 0;
    /** size of database after remove transaction*/
    int dbTranMiningLen = 0;
    /** size of Itemset*/
    int itemsetCount = 0;   
       
    /** if true, transaction merging will be performed by the algorithm */
    boolean activateTransactionMerging;

    /** A parameter for transaction merging*/
    final int MAXIMUM_SIZE_MERGING = 1000;
    
    /** number of times a transaction was read */
    long transactionReadingCount;
    /** number of merges */
    long mergeCount;

    /** number of itemsets from the search tree that were considered */
	private long candidateCount;
	private boolean activateSubtreeUtilityPruning;
    
	/** 
	 * Constructor
	 */
    public CoIUM() {
		candidateCount = 0;
		minUtil = 0;
	}

    public CoHUIList runAlgorithm(int minUtil, double minCor, String inputPath, String outputPath, 
    		boolean activateTransactionMerging, int transCount) 
    				throws IOException {
    	
    	// reset variables for statistics
    	mergeCount=0;
    	transactionReadingCount=0;
		timeIntersections = 0;
		timeDatabaseReduction = 0;
    	

    	this.activateSubtreeUtilityPruning = activateSubtreeUtilityPruning;
    	
		//long heapBefore = Runtime.getRuntime().totalMemory();
		
		// read the input file
    	this.transCount=transCount;
		Dataset dataset = new Dataset(inputPath, transCount);
		
		//long heapAfter = Runtime.getRuntime().totalMemory();

		// record the start time
		startTimestamp = System.currentTimeMillis();

		//dbSizeOnMem = (heapAfter-heapBefore)/1024/1024;
		dbSizeOnMem = 0;
		//System.out.println("DBSIZE="+((long) dbSizeOnMem)+"MB");
		
		// save minUtil value selected by the user
		this.minUtil = minUtil;
		this.minCor=minCor;

		// if the user choose to save to file
		// create object for writing the output file
		if(outputPath != null) {
			writer = new BufferedWriter(new FileWriter(outputPath));
		}else {
			// if the user choose to save to memory
			writer = null;
	        this.correlatedHighUtilityItemsets = new CoHUIList();
		}

		// reset the number of itemset found
		patternCount = 0;
		
		
		//Line 1
		int[] alpha=null;
		
		// reset the memory usage checking utility
		MemoryLogger.getInstance().reset();
		
		// if in debug mode, show the initial database in the console
		if(DEBUG)
		{
			System.out.println("===== Initial database === ");
			System.out.println(dataset.toString());
		}
		
	    // Scan the database using utility-bin array to calculate the TWU
		// of each item
		dbTranlen = dataset.getTransactions().size();
		itemsetCount = dataset.getMaxItem();
		
		
		//Line 2->4: Tính LU và sup(i) lần dầu tiên 
		useUtilityBinArrayToCalculateLocalUtilityFirstTime(dataset);
		
		// if in debug mode, show the TWU calculated using the utility-bin array
        if(DEBUG) 
        {
	    	System.out.println("===== TWU OF SINGLE ITEMS === ");
			for (int i = 1; i < utilityBinArrayLU.length; i++)
			{
				System.out.println("item : " + i + " twu: " + utilityBinArrayLU[i]);
			}
			System.out.println();			
        }

	   	//Line 5: Xác đinh secondary
	   	List<Integer> secondary = new ArrayList<Integer>();//itemToKeep
	   	for(int j=1; j< utilityBinArrayLU.length;j++) 
	   	{
	   		if(utilityBinArrayLU[j] >= minUtil) 
	   		{
	   			secondary.add(j);
	   		}
	   	}
	   	
      // Cap nhat lại độ hỗ trợ của cac item trong secondary
		newItemCount = secondary.size();		
	   
	   //Line 8: Sap xep cac item trong secondary theo TWU
	   insertionSort(secondary, utilityBinArrayLU);
       

       oldNameToNewNames = new int[dataset.getMaxItem() + 1];
       newNamesToOldNames = new int[dataset.getMaxItem() + 1];

       int currentName = 1;
       // For each item in increasing order of TWU
       for (int j=0; j< secondary.size(); j++)
	   {
    	   // get the item old name
    	   int item = secondary.get(j);
		   oldNameToNewNames[item] = currentName;
		   newNamesToOldNames[currentName] = item;
		   secondary.set(j, currentName);
		   currentName++;
	   }
       
     //cập nhật lại support của các item trong secondary
       int[] itemSupport = new int[newItemCount +1];
		for(int item:secondary)
			itemSupport[item]=Sups[newNamesToOldNames[item]];
  	
       
       
  

		if (DEBUG) {
			System.out.println(secondary);
			System.out.println(Arrays.toString(oldNameToNewNames));
			System.out.println(Arrays.toString(newNamesToOldNames));
		}	
    	
		 // We now loop over each transaction from the dataset
		// to remove unpromising items
    	for(int i=0; i< dataset.getTransactions().size();i++)
    	{
    		// Get the transaction
    		Transaction transaction  = dataset.getTransactions().get(i);
    		transaction.removeUnpromisingItems(oldNameToNewNames);
    	}
		

    	long timeStartSorting = System.currentTimeMillis();
    	// We only sort if transaction merging is activated
    	if(activateTransactionMerging){
    		// Sort the dataset using a new comparator
    		Collections.sort(dataset.getTransactions(), new Comparator<Transaction>(){
				@Override
				/**
				 * Compare two transactions
				 */
				public int compare(Transaction t1, Transaction t2) {
					int pos1 = t1.items.length - 1;
					int pos2 = t2.items.length - 1;

					if(t1.items.length < t2.items.length){
						while(pos1 >=0){
							int subtraction = t2.items[pos2]  - t1.items[pos1];
							if(subtraction !=0){
								return subtraction;
							}
							pos1--;
							pos2--;
						}
						// if they ware the same, they we compare based on length
						return -1;
						
					// else if the second transaction is smaller than the first one
					}else if (t1.items.length > t2.items.length){
						// while the current position in the second transaction is >0
						while(pos2 >=0){
							int subtraction = t2.items[pos2]  - t1.items[pos1];
							if(subtraction !=0){
								return subtraction;
							}
							pos1--;
							pos2--;
						}
						// if they ware the same, they we compare based on length
						return 1;

					}else{
					// else if both transactions have the same size
						while(pos2 >=0){
							int subtraction = t2.items[pos2]  - t1.items[pos1];
							if(subtraction !=0){
								return subtraction;
							}
							pos1--;
							pos2--;
						}
						// if they ware the same, they we compare based on length
						return 0;
					}
				}

    		});
		
        	// =======================REMOVE EMPTY TRANSACTIONS==========================
    		int emptyTransactionCount = 0;
        	for(int i=0; i< dataset.getTransactions().size();i++)
        	{
        		Transaction transaction  = dataset.getTransactions().get(i);
        		if(transaction.items.length == 0){
        			emptyTransactionCount++;
        		}
        	}
        	dataset.transactions = dataset.transactions.subList(emptyTransactionCount, dataset.transactions.size());        	
    	}
    	

    	// record the total time spent for sorting
    	timeSort = System.currentTimeMillis() - timeStartSorting;
    	
    	// if in debug mode, print the database after sorting and removing promising items
		if(DEBUG)
		{
			System.out.println("===== Database without unpromising items and sorted by TWU increasing order === ");
			System.out.println(dataset.toString());
		}
		
		// initialize the utility-bin array for counting the subtree utility
		dbTranMiningLen = dataset.getTransactions().size();
		
		if(dbTranMiningLen !=0)
		{
			utilityBinArraySU = new int[newItemCount + 1];			
	    	    		
	    	useUtilityBinArrayToCalculateSubtreeUtilityFirstTime(dataset);	    
	    		    	
	    	// Calculate the set of items that pass the sub-tree utility pruni;ng condition
			List<Integer> itemsToExplore = new ArrayList<Integer>();
			List<Integer> primary = new ArrayList<Integer>();
			// if subtree utility pruning is activated

			for(Integer item : secondary){
				// if the subtree utility is higher or equal to minutil, then keep it
				if (utilityBinArraySU[item] >= minUtil) {
					primary.add(item);
				}
				
			}
	    	
	
			// If in debug mode, show the list of promising items
	    	if(DEBUG)
	    	{
		       	System.out.println("===== List of promising items === ");
		       	System.out.println(secondary);
	    	}
	
	    	//======
	        // Recursive call to the algorithm
	       	// If subtree utility pruning is activated
    		// We call the recursive algorithm with the database, secondary items and primary items
    		backtrackingEFIM(dataset.getTransactions(), secondary, primary, 0);

		}
		// record the end time
		endTimestamp = System.currentTimeMillis();
		
		//close the output file
		if(writer != null) {
			writer.close();
		}
		
		// check the maximum memory usage
		MemoryLogger.getInstance().checkMemory();
		printPeakHeapUsage();
		
		// return the set of high-utility itemsets
        return correlatedHighUtilityItemsets;
    }

	/**
	 * Implementation of Insertion sort for sorting a list of items by increasing order of TWU.
	 * This has an average performance of O(n log n)
	 * @param items list of integers to be sorted
	 * @param items list the utility-bin array indicating the TWU of each item.
	 */
	public static void insertionSort(List<Integer> items, int [] utilityBinArrayTWU){
		// the following lines are simply a modified an insertion sort
		
		for(int j=1; j< items.size(); j++){
			Integer itemJ = items.get(j);
			int i = j - 1;
			Integer itemI = items.get(i);
			
			// we compare the twu of items i and j
			int comparison = utilityBinArrayTWU[itemI] - utilityBinArrayTWU[itemJ];
			// if the twu is equal, we use the lexicographical order to decide whether i is greater
			// than j or not.
			if(comparison == 0){
				comparison = itemI - itemJ;
			}
			
			while(comparison > 0){
				items.set(i+1, itemI);

				i--;
				if(i<0){
					break;
				}
				
				itemI = items.get(i);
				comparison = utilityBinArrayTWU[itemI] - utilityBinArrayTWU[itemJ];
				// if the twu is equal, we use the lexicographical order to decide whether i is greater
				// than j or not.
				if(comparison == 0){
					comparison = itemI - itemJ;
				}
			}
			items.set(i+1,itemJ);
		}
	}
	 /**
     * Recursive method to find all high-utility itemsets
     * @param the list of transactions containing the current prefix P
	 * @param itemsToKeep the list of secondary items in the p-projected database
	 * @param itemsToExplore the list of primary items in the p-projected database
	 * @param the current prefixLength
     * @throws IOException if error writing to output file
     */
    private void backtrackingEFIM( List<Transaction> transactionsOfP,
    		List<Integer> itemsToKeep, List<Integer> itemsToExplore, int prefixLength) throws IOException {

    	// update the number of candidates explored so far
		candidateCount += itemsToExplore.size();
    	
        // ========  for each frequent item  e  =============
		for (int j = 0; j < itemsToExplore.size(); j++) {
			Integer e = itemsToExplore.get(j);

			// ========== PERFORM INTERSECTION =====================
			// Calculate transactions containing P U {e} 
			// At the same time project transactions to keep what appears after "e"
	        List<Transaction> transactionsPe = new ArrayList<Transaction>();
	        
	        // variable to calculate the utility of P U {e}
			int utilityPe = 0;		

			// For merging transactions, we will keep track of the last transaction read
			// and the number of identical consecutive transactions
			Transaction previousTransaction = null;
			int consecutiveMergeCount = 0;
			
	        // this variable is to record the time for performing intersection
			long timeFirstIntersection = System.currentTimeMillis();
			
			// For each transaction
	        for(Transaction transaction : transactionsOfP) {
	        	// Increase the number of transaction read
	        	transactionReadingCount++;
	        	
	        	// To record the time for performing binary searh
	        	long timeBinaryLocal = System.currentTimeMillis();
	        	
	        	// we remember the position where e appears.
	        	// we will call this position an "offset"
	        	int positionE = -1;
	        	// Variables low and high for binary search
	    		int low = transaction.offset;
	    		int high = transaction.items.length - 1;

	    		// perform binary search to find e in the transaction
	    		while (high >= low ) {
	    			int middle = (low + high) >>> 1; // divide by 2
	    			if (transaction.items[middle] < e) {
	    				low = middle + 1;
	    			}else if (transaction.items[middle] == e) {
	    				positionE =  middle;
	    				break;
	    			}  else{
	    				high = middle - 1;
	    			}
	    		}
	    		// record the time spent for performing the binary search
	        	timeBinarySearch +=  System.currentTimeMillis() - timeBinaryLocal;
	        	
//	        	if(prefixLength == 0 && newNamesToOldNames[e] == 385) {
//		        	for(int i=0; i < transaction.getItems().length; i++) {
//		        		if(transaction.getItems()[i] == e) {
//		        			innerSum += transaction.getUtilities()[i];
//		        		}
//		        	}
//		        }
	        	
	        	// if 'e' was found in the transaction
	            if (positionE > -1  ) { 
	 
	            	// optimization: if the 'e' is the last one in this transaction,
	            	// we don't keep the transaction
					if(transaction.getLastPosition() == positionE){
						// but we still update the sum of the utility of P U {e}
						utilityPe  += transaction.utilities[positionE] + transaction.prefixUtility;
					}else{
						// otherwise
		            	if(activateTransactionMerging && MAXIMUM_SIZE_MERGING >= (transaction.items.length - positionE)){
			            	// we cut the transaction starting from position 'e'							
		            		Transaction projectedTransaction = new Transaction(transaction, positionE);
							utilityPe  += projectedTransaction.prefixUtility;
							
							// if it is the first transaction that we read
							if(previousTransaction == null){
								// we keep the transaction in memory 
								previousTransaction = projectedTransaction;
							}else if (isEqualTo(projectedTransaction, previousTransaction)){
								// If it is not the first transaction of the database and 
								// if the transaction is equal to the previously read transaction,
								// we will merge the transaction with the previous one
								
								// increase the number of consecutive transactions merged
								mergeCount++;
								
								// if the first consecutive merge
								if(consecutiveMergeCount == 0){
									// copy items and their profit from the previous transaction
									int itemsCount = previousTransaction.items.length - previousTransaction.offset;
									int[] items = new int[itemsCount];
									System.arraycopy(previousTransaction.items, previousTransaction.offset, items, 0, itemsCount);
									int[] utilities = new int[itemsCount];
									System.arraycopy(previousTransaction.utilities, previousTransaction.offset, utilities, 0, itemsCount);
									
									// make the sum of utilities from the previous transaction
							    	int positionPrevious = 0;
									int positionProjection = projectedTransaction.offset;
									while(positionPrevious < itemsCount){
										utilities[positionPrevious] += projectedTransaction.utilities[positionProjection];
										positionPrevious++;
										positionProjection++;
									}
									
									// make the sum of prefix utilities
									int sumUtilities = previousTransaction.prefixUtility += projectedTransaction.prefixUtility;
									
									// create the new transaction replacing the two merged transactions
									previousTransaction = new Transaction(items, utilities, previousTransaction.transactionUtility + projectedTransaction.transactionUtility);
									previousTransaction.prefixUtility = sumUtilities;	
	
								}else{
									// if not the first consecutive merge
	
									// add the utilities in the projected transaction to the previously
									// merged transaction
							    	int positionPrevious = 0;
									int positionProjected = projectedTransaction.offset;
									int itemsCount = previousTransaction.items.length;
									while(positionPrevious < itemsCount){
										previousTransaction.utilities[positionPrevious] += projectedTransaction.utilities[positionProjected];
										positionPrevious++;
										positionProjected++;
									}
									
									// make also the sum of transaction utility and prefix utility
									previousTransaction.transactionUtility += projectedTransaction.transactionUtility;
									previousTransaction.prefixUtility += projectedTransaction.prefixUtility;	
								}
								// increment the number of consecutive transaction merged
								consecutiveMergeCount++;
							}else{
								// if the transaction is not equal to the preceding transaction
								// we cannot merge it so we just add it to the database
								transactionsPe.add(previousTransaction);
								// the transaction becomes the previous transaction
								previousTransaction = projectedTransaction;
								// and we reset the number of consecutive transactions merged
								consecutiveMergeCount = 0;
							}
						}else{
			            	// Otherwise, if merging has been deactivated
							// then we just create the projected transaction
							Transaction projectedTransaction = new Transaction(transaction, positionE);
							// we add the utility of Pe in that transaction to the total utility of Pe
							utilityPe  += projectedTransaction.prefixUtility;
							// we put the projected transaction in the projected database of Pe
							transactionsPe.add(projectedTransaction);
						}
					}
					// This is an optimization for binary search:
					// we remember the position of E so that for the next item, we will not search
					// before "e" in the transaction since items are visited in lexicographical order
		            transaction.offset = positionE;   
	            }else{
					// This is an optimization for binary search:
					// we remember the position of E so that for the next item, we will not search
					// before "e" in the transaction since items are visited in lexicographical order
	            	transaction.offset = low;
	            }
	        }
	        // remember the total time for peforming the database projection
	        timeIntersections += (System.currentTimeMillis() - timeFirstIntersection);

	        // Add the last read transaction to the database if there is one
	        if(previousTransaction != null){
	        	transactionsPe.add(previousTransaction);
	        }
	        
	        // Append item "e" to P to obtain P U {e}
	        // but at the same time translate from new name of "e"  to its old name
	        temp[prefixLength] = newNamesToOldNames[e];
     
	        // if the utility of PU{e} is enough to be a high utility itemset
	        if(utilityPe  >= minUtil)
	        {
	        	// output PU{e}
	        	output(prefixLength, utilityPe );
	        }

			//==== Next, we will calculate the Local Utility and Sub-tree utility of
	        // all items that could be appended to PU{e} ====
	        useUtilityBinArraysToCalculateUpperBounds(transactionsPe, j, itemsToKeep);  
			
	        // we now record time for identifying promising items
			long initialTime = System.currentTimeMillis();
			
			// We will create the new list of secondary items
			List<Integer> newItemsToKeep = new ArrayList<Integer>();
			// We will create the new list of primary items
			List<Integer> newItemsToExplore = new ArrayList<Integer>();
			
			// for each item
	    	for (int k = j+1; k < itemsToKeep.size(); k++) {
	        	Integer itemk =  itemsToKeep.get(k);
	        	
	        	// if the sub-tree utility is no less than min util
	            if(utilityBinArraySU[itemk] >= minUtil) {
	            	// and if sub-tree utility pruning is activated
	            	if(activateSubtreeUtilityPruning){
	            		// consider that item as a primary item
	            		newItemsToExplore.add(itemk);
	            	}
	            	// consider that item as a secondary item
	            	newItemsToKeep.add(itemk);
	            }else if(utilityBinArrayLU[itemk] >= minUtil)
	            {
	            	// otherwise, if local utility is no less than minutil,
	            	// consider this itemt to be a secondary item
	            	newItemsToKeep.add(itemk);
	            }
	        }
	    	// update the total time  for identifying promising items
	    	timeIdentifyPromisingItems +=  (System.currentTimeMillis() -  initialTime);
			
			// === recursive call to explore larger itemsets
	    	if(activateSubtreeUtilityPruning){
	    		// if sub-tree utility pruning is activated, we consider primary and secondary items
	    		backtrackingEFIM(transactionsPe, newItemsToKeep, newItemsToExplore,prefixLength+1);
	    	}else{
	    		// if sub-tree utility pruning is deactivated, we consider secondary items also
	    		// as primary items
	    		backtrackingEFIM(transactionsPe, newItemsToKeep, newItemsToKeep,prefixLength+1);
	    	}
		}

		// check the maximum memory usage for statistics purpose
		MemoryLogger.getInstance().checkMemory();
	}

    /**
     * Check if two transaction are identical
     * @param t1  the first transaction
     * @param t2  the second transaction
     * @return true if they are equal
     */
    private boolean isEqualTo(Transaction t1, Transaction t2) {
    	// we first compare the transaction lenghts
		int length1 = t1.items.length - t1.offset;
		int length2 = t2.items.length - t2.offset;
		// if not same length, then transactions are not identical
    	if(length1 != length2){
    		return false;
    	}
    	// if same length, we need to compare each element position by position,
    	// to see if they are the same
    	int position1 = t1.offset;
		int position2 = t2.offset;
		
		// for each position in the first transaction
		while(position1 < t1.items.length){
			// if different from corresponding position in transaction 2
			// return false because they are not identical
			if(t1.items[position1]  != t2.items[position2]){
				return false;
			}
			// if the same, then move to next position
			position1++;
			position2++;
		}
		// if all items are identical, then return to true
		return true;
	}

   
	/**
	 * Scan the initial database to calculate the local utility of each item
	 * using a utility-bin array
	 * @param dataset the transaction database
	 */
	public void useUtilityBinArrayToCalculateLocalUtilityFirstTime(Dataset dataset) {

		// Initialize utility bins for all items
		utilityBinArrayLU = new int[dataset.getMaxItem() + 1];
		Sups = new int[dataset.getMaxItem() + 1];
		
		// Scan the database to fill the utility bins
		// For each transaction
		for (Transaction transaction : dataset.getTransactions()) {
			// for each item
			for(Integer item: transaction.getItems()) {
				// we add the transaction utility to the utility bin of the item
				utilityBinArrayLU[item] += transaction.transactionUtility;
				Sups[item]++;
			}
		}
	}	
	/**
	 * Scan the initial database to calculate the sub-tree utility of each item
	 * using a utility-bin array
	 * @param dataset the transaction database
	 */
	public void useUtilityBinArrayToCalculateSubtreeUtilityFirstTime(Dataset dataset) {

		int sumSU;
		for (Transaction transaction : dataset.getTransactions()) {
			sumSU = 0;
    	   for(int i = transaction.getItems().length-1; i >=0; i--) {
    		   Integer item = transaction.getItems()[i];
    		   sumSU += transaction.getUtilities()[i];
    		   utilityBinArraySU[item] += sumSU;
        	}
		}
	}

    /**
     * Utilize the utility-bin arrays to calculate the sub-tree utility and local utility of all
     * items that can extend itemset P U {e}
     * @param transactions the projected database for P U {e}
     * @param j the position of j in the list of promising items
     * @param itemsToKeep the list of promising items
     */
    private void useUtilityBinArraysToCalculateUpperBounds(List<Transaction> transactionsPe, 
    		int j, List<Integer> itemsToKeep) {

    	// we will record the time used by this method for statistics purpose
		long initialTime = System.currentTimeMillis();
		
		// For each promising item > e according to the total order
		for (int i = j + 1; i < itemsToKeep.size(); i++) {
			Integer item = itemsToKeep.get(i);
			// We reset the utility bins of that item for computing the sub-tree utility and
			// local utility
			utilityBinArraySU[item] = 0;
			utilityBinArrayLU[item] = 0;
		}

		int sumRemainingUtility;
		// for each transaction
		for (Transaction transaction : transactionsPe) {
			// count the number of transactions read
			transactionReadingCount++;
			
			// We reset the sum of reamining utility to 0;
			sumRemainingUtility = 0;
			// we set high to the last promising item for doing the binary search
			int high = itemsToKeep.size() - 1;

			// for each item in the transaction that is greater than i when reading the transaction backward
			// Note: >= is correct here. It should not be >.
			for (int i = transaction.getItems().length - 1; i >= transaction.offset; i--) {
				// get the item
				int item = transaction.getItems()[i];
				
				// We will check if this item is promising using a binary search over promising items.

				// This variable will be used as a flag to indicate that we found the item or not using the binary search
				boolean contains = false;
				// we set "low" for the binary search to the first promising item position
				int low = 0;

				// do the binary search
				while (high >= low) {
					int middle = (low + high) >>> 1; // divide by 2
					int itemMiddle = itemsToKeep.get(middle);
					if (itemMiddle == item) {
						// if we found the item, then we stop
						contains = true;
						break;
					} else if (itemMiddle < item) {
						low = middle + 1;
					} else {
						high = middle - 1;
					}  
				}
				// if the item is promising
				if (contains) {
					// We add the utility of this item to the sum of remaining utility
					sumRemainingUtility += transaction.getUtilities()[i];
					// We update the sub-tree utility of that item in its utility-bin
					utilityBinArraySU[item] += sumRemainingUtility + transaction.prefixUtility;
					// We update the local utility of that item in its utility-bin
					utilityBinArrayLU[item] += transaction.transactionUtility + transaction.prefixUtility;
				}
			}
		}
		// we update the time for database reduction for statistics purpose
		timeDatabaseReduction += (System.currentTimeMillis() - initialTime);
    }
    /**
     * Utilize the utility-bin arrays to calculate the sub-tree utility and local utility of all
     * items that can extend itemset P U {e}
     * @param transactions the projected database for P U {e}
     * @param j the position of j in the list of promising items
     * @param itemsToKeep the list of promising items
     */
  

    /**
     * Save a high-utility itemset to file or memory depending on what the user chose.
     * @param itemset the itemset
     * @throws IOException if error while writting to output file
     */
    private void output(int tempPosition, int utility) throws IOException {
        patternCount++;
        /*
        // if user wants to save the results to memory
		if (writer == null) {
			// we copy the temporary buffer into a new int array
			int[] copy = new int[tempPosition+1];
			System.arraycopy(temp, 0, copy, 0, tempPosition+1);
			// we create the itemset using this array and add it to the list of itemsets
			// found until now
			highUtilityItemsets.addItemset(new Itemset(copy, utility),copy.length); 
		} else {
			// if user wants to save the results to file
			// create a stringuffer
			StringBuffer buffer = new StringBuffer();
			// append each item from the itemset to the stringbuffer, separated by spaces
			for (int i = 0; i <= tempPosition; i++) {
				buffer.append(temp[i]);
				if (i != tempPosition) {
					buffer.append(' ');
				}
			}
			// append the utility of the itemset
			buffer.append(" #UTIL: ");
			buffer.append(utility);
			
			// write the stringbuffer to file and create a new line
			// so that we are ready for writing the next itemset.
			writer.write(buffer.toString());
			writer.newLine();
		}
		*/
    }


    /**
     * Print out the maximum peak heap usage
     */
    public static void printPeakHeapUsage()
    {
    	try {
            List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
            // we print the result in the console
			double total = 0;
			for (MemoryPoolMXBean memoryPoolMXBean : pools) {
				if (memoryPoolMXBean.getType() == MemoryType.HEAP) {
					long peakUsed = memoryPoolMXBean.getPeakUsage().getUsed();
					//System.out.println(String.format("Peak used for: %s is %.2f", memoryPoolMXBean.getName(), (double)peakUsed/1024/1024));
					total = total + peakUsed;
				}
			}
			System.out.println(String.format("Total heap peak used: %f MB", ((total)/1024/1024)-dbSizeOnMem));
     
       } catch (Throwable t) {
            System.err.println("Exception in agent: " + t);
       }
    }	 
 
    /**
     * Print statistics about the latest execution of the EFIM algorithm.
     */
	public void printStats() {

		System.out.println("========== iMEFIM - STATS ============");
		System.out.println(" minUtil = " + minUtil);
		System.out.println(" High utility itemsets count: " + patternCount);	
		System.out.println(" Total time ~: " + (endTimestamp - startTimestamp)
				+ " ms");
		System.out.println(" Transaction merge count ~: " + mergeCount);	
		System.out.println(" Transaction read count ~: " + transactionReadingCount);	

		// if in debug mode, we show more information
		if(DEBUG) {
			
			System.out.println(" Time intersections ~: " + timeIntersections
					+ " ms");	
			System.out.println(" Time database reduction ~: " + timeDatabaseReduction
					+ " ms");
			System.out.println(" Time promising items ~: " + timeIdentifyPromisingItems
					+ " ms");
			System.out.println(" Time binary search ~: " + timeBinarySearch
					+ " ms");
			System.out.println(" Time sort ~: " + timeSort	+ " ms");
		}
		System.out.println(" Max memory:" + (MemoryLogger.getInstance().getMaxMemory()-dbSizeOnMem));
		System.out.println(" Candidate count : "             + candidateCount);
		System.out.println("=====================================");
	}
	/**
     * Print statistics about the latest execution of the NewEFIM algorithm to file
     */
	public void printLog(String output) throws IOException 
	{
		BufferedWriter writerlog = new BufferedWriter(new FileWriter(output,true));
		writerlog.newLine();
		writerlog.write("========== iMEFIM - STATS ============"); writerlog.newLine();
		writerlog.write(" minUtil = " + minUtil);writerlog.newLine();
		
		writerlog.write(" Number Transaction = " + dbTranlen);writerlog.newLine();
		writerlog.write(" Number item = " + itemsetCount);writerlog.newLine();
		writerlog.write(" Number Transaction after remove item = " + dbTranMiningLen);writerlog.newLine();
		writerlog.write(" Number item after remove item= " + newItemCount);writerlog.newLine();
		
		writerlog.write(" High utility itemsets count: " + patternCount);writerlog.newLine();	
		writerlog.write(" Total time ~: " + (endTimestamp - startTimestamp)+ " ms");writerlog.newLine();
		writerlog.write(" Transaction merge count ~: " + mergeCount);	writerlog.newLine();
		writerlog.write(" Transaction read count ~: " + transactionReadingCount);	writerlog.newLine();
			
		writerlog.write(" Time intersections ~: " + timeIntersections + " ms");	writerlog.newLine();
		writerlog.write(" Time database reduction ~: " + timeDatabaseReduction + " ms");writerlog.newLine();
		writerlog.write(" Time promising items ~: " + timeIdentifyPromisingItems	+ " ms");writerlog.newLine();
		writerlog.write(" Time binary search ~: " + timeBinarySearch	+ " ms");writerlog.newLine();
		writerlog.write(" Time sort ~: " + timeSort	+ " ms");	writerlog.newLine();
		writerlog.write(" Max memory:" + MemoryLogger.getInstance().getMaxMemory());writerlog.newLine();
		writerlog.write(" Candidate count : "             + candidateCount);writerlog.newLine();
		writerlog.write("=====================================");
		writerlog.close();
		System.out.println("Finish");
	}
	
	public void printLog(String output, String dbName) throws IOException 
	{
		int times=(int) (endTimestamp - startTimestamp);
		int memories=(int) (MemoryLogger.getInstance().getMaxMemory()-dbSizeOnMem);
		BufferedWriter writerlog = new BufferedWriter(new FileWriter(output,true));
		writerlog.newLine();
		writerlog.write("========== CoIUM ============"); writerlog.newLine();
		writerlog.write(" Dataset = " + dbName);writerlog.newLine();
		writerlog.write(" minUtil = " + this.minUtil);writerlog.newLine();
		writerlog.write(" Mincor = " + this.minCor);writerlog.newLine();
		writerlog.write(" Number of Trans = " + this.transCount);writerlog.newLine();
		
		writerlog.write(" Number of Cohuis= " + patternCount);writerlog.newLine();
		writerlog.write(" Number of Candidate = " + this.candidateCount);writerlog.newLine();
		writerlog.write(" Times (ms)= " +times);writerlog.newLine();
		
		writerlog.write(" Max memory:" + memories);writerlog.newLine();
		writerlog.write("=====================================");
		writerlog.close();
		System.out.println("Finish");
	}
	
}
