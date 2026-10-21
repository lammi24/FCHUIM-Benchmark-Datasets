package AlgCoHUI_Miner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * This class represents a set of itemsets, where an itemset is an array of integers 
 * with an associated support count. Itemsets are ordered by size. For
 * example, level 1 means itemsets of size 1 (that contains 1 item).
* 
 * @author Philippe Fournier-Viger
 */

public class Itemsets {

	/** We store the itemsets in a list named "levels".
	 Position i in "levels" contains the list of itemsets of size i */
	private final List<List<Itemset>> levels = new ArrayList<List<Itemset>>(); 
	/** the total number of itemsets **/
	private int itemsetsCount = 0;
	/** a name that we give to these itemsets (e.g. "frequent itemsets") */
	private String name;

	/**
	 * Constructor
	 * @param name the name of these itemsets
	 */
	public Itemsets(String name) {
		this.name = name;
		levels.add(new ArrayList<Itemset>()); // We create an empty level 0 by
												// default.
	}

	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#printItemsets(int)
	 */
	public void printItemsets() {
		System.out.println(" ------- " + name + " -------");
		int patternCount = 0;
		int levelCount = 0;
		// for each level (a level is a set of itemsets having the same number of items)
		for (List<Itemset> level : levels) {
			// print how many items are contained in this level
			System.out.println("  L" + levelCount + " ");
			for (Itemset itemset : level) {
				Arrays.sort(itemset.getItems());
				// print the itemset
				System.out.print("  pattern " + patternCount + ":  " +itemset.toString());
				System.out.print("Utility :  " 
						+ itemset.getUtility());
				patternCount++;
				System.out.println(" ");
			}
			levelCount++;
		}
		System.out.println(" --------------------------------");
	}
	
	public void fileprintItemsets(String output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		//writer.write(" ------- " + name + " -------");
		//writer.newLine();
	
		int patternCount = 0;
		int levelCount = 0;
		// for each level (a level is a set of itemsets having the same number of items)
		for (List<Itemset> level : levels) {
			// print how many items are contained in this level
			//writer.write("  L" + levelCount + " ");
			//writer.newLine();
			for (Itemset itemset : level) {
				Arrays.sort(itemset.getItems());
				// print the itemset
				writer.write(itemset.toString());
				writer.write(":" + itemset.getUtility());
				patternCount++;
				//System.out.println(" ");
				writer.newLine();
			}
			levelCount++;
		}
		
		//writer.write(" --------------------------------");
		//writer.newLine();
		writer.close();
	}


	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#addItemset(ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset, int)
	 */
	public void addItemset(Itemset itemset, int k) {
		while (levels.size() <= k) {
			levels.add(new ArrayList<Itemset>());
		}
		levels.get(k).add(itemset);
		itemsetsCount++;
	}
	
	public void addItemsetInsertionSort(Itemset itemset, int k) {
		while (levels.size() <= k) {
			levels.add(new ArrayList<Itemset>());
		}
		int n=levels.get(k).size();
		itemsetsCount++;
		if(n==0)
			levels.get(k).add(itemset);
		else
		{
			ArrayList<Itemset> l=(ArrayList<Itemset>) levels.get(k);
			int support=itemset.support;
			int left=0;
			int right=n-1;
			do
			{
				int mid=(left+right)/2;
				int sup=l.get(mid).support;
				if(sup==support)
				{
					l.add(mid+1, itemset);
					return;
				}
				if(sup>support)
					right=mid-1;
				else
					left=mid+1;
					
			}while(left<=right);
			l.add(left, itemset);
		}
		
	}

	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#getLevels()
	 */
	public List<List<Itemset>> getLevels() {
		return levels;
	}

	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#getItemsetsCount()
	 */
	public int getItemsetsCount() {
		return itemsetsCount;
	}

	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#setName(java.lang.String)
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	/* (non-Javadoc)
	 * @see ca.pfv.spmf.patterns.itemset_array_integers_with_count.AbstractItemsets#decreaseItemsetCount()
	 */
	public void decreaseItemsetCount() {
		itemsetsCount--;
	}
	
	public void sort()
	{
		int n=levels.size();
		for(int i=0;i<n;i++)
		{
			Collections.sort(levels.get(i));
		}
	}
	
	public boolean hasNoBackward(Itemset itemset, int k)//kiem tra itemset có k phantu
	{
		int n=levels.size();
		if(k>=n)
			return false;
		int sup=itemset.support;
		
		
		for(int i=k+1;i<n;i++)
		{
			ArrayList<Itemset> lst=(ArrayList<Itemset>) levels.get(i);
			int soluong=lst.size();
			if(soluong==0)
				continue;
			int left=0;
			int right=soluong-1;
			int bang=0;
			do
			{
				int mid=(left+right)/2;
				int support=lst.get(mid).support;
				if(support==sup)
				{
					bang=mid;
					break;
				}
				if(support>sup)
					right=mid-1;
				else
					left=mid+1;
			}while(left<=right);
			
			if(left<=right)
			{
				int truoc=bang;
				while(truoc >=0 && sup==lst.get(truoc).support)
				{
					if(lst.get(truoc).isContains(itemset, k))
						return true;
					else
						truoc--;
				}
				int sau=bang+1;
				while(sau <soluong && sup==lst.get(sau).support)
				{
					if(lst.get(sau).isContains(itemset, k))
						return true;
					else
						sau++;
				}
			}
			
			/*
			//dang tim kiem tuyến tính
			for(int j=0;j<soluong;j++)
			{
				Itemset item=lst.get(j);
				if(item.support>sup)
					break;
				if(item.support==sup && item.isContains(itemset, k))
					return true;
			}	
			//kết thúc tìm kiếm tuyến tinh
			 */
		}
		return false;
		
	}

	
	
}
