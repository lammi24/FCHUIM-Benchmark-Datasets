package AlgCoHUI_Miner;

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
import java.util.HashMap;
import java.util.List;



public class HMinerCo {
	
	int minUtil;
	float minCor;
	
	int twus[];
	int supports[];
	int supps[];
	WCUL oneItemWCURs[];
	int [][] EUCS;
	int newItemCount;
	int patternCount;
	int candidateCount=0;
	
	long times=0;
	int G1Eden=0;
	int G1OldGen=0;
	int G1Suvivor=0;
	public static int G1Total=0;
	int SoTran=0;
	
	
	Dataset dataset;
	List<CoHUI> lstCoHUIs;
	
	
	long startTimestamp;//thá»�i gian báº¯t Ä‘áº§u cháº¡y
	long endTimestamp;//thá»�i gian káº¿t thÃºc
	
	
	public void runAglo(String inputPath, int minUtility, float minCorr, int so) throws IOException
	{
		this.SoTran=so;
		startTimestamp=System.currentTimeMillis();
		patternCount=0;
		this.minUtil = minUtility;
		this.minCor=minCorr;
		this.lstCoHUIs =new ArrayList<CoHUI>();
		
		
		dataset = new Dataset(inputPath, so);
	    calculateSUP_TWU();
	    
//	    for(int twu:twus)
//	    	System.out.print(twu + " ");
//	    System.out.println();
//	    for(int s:supports)
//	    	System.out.print(s + " ");
//	    System.out.println();
	    List<Integer> itemsToKeep = new ArrayList<Integer>();

	   	for(int j=1; j< twus.length;j++) 
	   	{
	   		if(twus[j] >= minUtil)
	   			itemsToKeep.add(j);
	   	}
	   	
//	   	System.out.println(itemsToKeep.toString());
	   	
	   	Collections.sort(itemsToKeep,new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if(supports[o1]<supports[o2])
					return -1;
				if(supports[o1]>supports[o2])
					return 1;
				return o1.compareTo(o2);
			}
		});
	   	
//		System.out.println(itemsToKeep.toString());
		
		
		int[] oldNameToNewNames = new int[dataset.getMaxItem() + 1];
	    int[] newNamesToOldNames = new int[dataset.getMaxItem() + 1];
		
	    //supps=new int[]
	     int currentName = 1;
	     for (int j=0; j< itemsToKeep.size(); j++)
		   {
	    	   // get the item old name
	    	   int item = itemsToKeep.get(j);
	    	   // give it the new name
			   oldNameToNewNames[item] = currentName;
			   // remember its old name
			   newNamesToOldNames[currentName] = item;
			   // replace its old name by the new name in the list of promising items
			   itemsToKeep.set(j, currentName);
			   // increment by one the current name so that 
			   currentName++;
		   }
	       
	     
	     supps=new int[itemsToKeep.size()+1];
	     for(int i=1;i<=itemsToKeep.size();i++)
	     {
	    	 supps[i]=supports[newNamesToOldNames[i]];
	     }
	        // remember the number of promising item
//	     for(int s:supps)
//	    	 System.out.print(s + " ");
		newItemCount = itemsToKeep.size();
		int dem=0;
		for(int i=0; i< dataset.getTransactions().size();i++)
		{
	 		Transaction transaction  = dataset.getTransactions().get(i);
	 		transaction.removeUnpromisingItems(oldNameToNewNames);
		 		if(transaction.tu==0)
		 			dem++;
		}

//		dataset.maxItem=newItemCount;
//		System.out.println(dataset);
		
		constructOneWCULs();
//		System.out.println("****** Day lÃ  CUl 1-item");
//		for(int i=0;i<newItemCount;i++)
//		{
//			System.out.println(oneItemWCURs[i]);
//		}
		
		buildEUCS();
		searchHUI(null, oneItemWCURs);
		endTimestamp=System.currentTimeMillis();
		times=(endTimestamp-startTimestamp);
		System.out.println("minUtil: " + this.minUtil) ;
		System.out.println("Mincorr =" + this.minCor);
		System.out.println("Thoi gian thuc hien: " + times + " ms");
		System.out.println("CandidateCount =" +candidateCount);
		System.out.println("So luong CoHUI: " + lstCoHUIs.size());
		int stt=0;
//		for(CoHUI co:lstCoHUIs)
//		{
//			stt++;
//			System.out.print(stt + ":");
//			co.xuat();
//		}
//		
		
		//chuyen vá»� nhÆ° cu
		for(CoHUI co:lstCoHUIs)
		{
			//chuyen vá»� cÅ©
			for(int index=0;index<co.itemset.length;index++)
			{
				co.itemset[index]=newNamesToOldNames[co.itemset[index]];
			}
			Arrays.sort(co.itemset);
		}
		
//		System.out.println();
//		stt=0;
//		for(CoHUI co:lstCoHUIs)
//		{
//			stt++;
//			System.out.print(stt + ":");
//			co.xuat();
//		}
		
//		
//		for(int i=0;i<oneItemWCURs.length;i++)
//		{
//			WCUL wcul=oneItemWCURs[i];
//			long u=wcul.CU + wcul.NU;
//			long ru=wcul.CRU + wcul.NRU;
//			if(u>=minUtil)
//			{
//				//them vao CoHUIs
//				CoHUI coHui=new CoHUI(wcul.item);
//				coHui.cor=1.0f;
//				coHui.support=supps[wcul.item];
//				coHui.utility=u;
//				lstCoHUIs.add(coHui);
//				System.out.println("CoHUI: " + wcul.item);
//			}
//			if(u+ ru >minUtil)
//			{
//				WCUL[] exWculs=construct(1, wcul.item, oneItemWCURs, i+1);
//				//gá»�i ham search
//				
//			}
//		}
	
	}
	
	void searchHUI(int[] PreItemset, WCUL[] wculs)
	{
		int sizeCUL=wculs.length;
		int n;
		if (PreItemset==null)
			n=0;
		else
			n=PreItemset.length;
		
		for(int i=0;i<sizeCUL;i++)
		{
			this.candidateCount++;
			WCUL curCUL=wculs[i];
			int u=curCUL.CU + curCUL.NU;
			int ru=curCUL.CRU + curCUL.NRU;
			float cor=curCUL.COR;
			
			int x=curCUL.item;
			
			int itemset[] = new int[n+1];
			for(int k=0;k<n;k++)
				itemset[k]=PreItemset[k];
			itemset[n]=x;

			if(cor>=minCor)
			{
				if(u >=minUtil)
				{
					
					//them vao CoHUIs
					CoHUI coHui=new CoHUI();
					
					coHui.itemset=itemset;
					coHui.cor=cor;
					coHui.support=curCUL.NSUP+curCUL.CSUP;;
					coHui.utility=u;
					lstCoHUIs.add(coHui);				
					patternCount++;
				}
				if(u+ru>=minUtil)
				{
					//xu ly tiep
					WCUL[] exCULs=construct(n+1, x, wculs,i+1);
//					System.out.println("xuong dong: ");
					//if(exCULs!=null)
//					for(WCUL cc:exCULs)
//						System.out.println(cc);
					if(exCULs!=null)
					{
						searchHUI(itemset, exCULs);//dong nay cÃ³ váº¥n Ä‘á»�
					}
				}
			}
		}		
	}
	
	
	
	
	
	public void calculateSUP_TWU() {
		twus = new int[dataset.getMaxItem() + 1];
		supports=new int[dataset.getMaxItem() + 1];
		long tong=0;
		for (Transaction transaction : dataset.getTransactions()) {
			tong=tong+transaction.tu;
			for(Integer item: transaction.getItems()) {
				twus[item] += transaction.tu;
				supports[item]++;
			}
		}
		System.out.println("Tong TWU la: " + tong);
	}
	
	public void buildEUCS()
	{
		EUCS=new int[newItemCount+1][newItemCount+1];
		for(Transaction tran:dataset.transactions)
	   	{
			int soluong=tran.items.length;
			long tu= tran.tu;
	   		for(int i=0;i<soluong-1;i++)
	   			for(int j=i+1;j<soluong;j++)
	   				EUCS[tran.items[i]][tran.items[j]]+=tu;
	   	}
	}
	
	public void constructOneWCULs()
	{
		oneItemWCURs=new WCUL[newItemCount];    	
		for(int i=0;i<newItemCount;i++)
		{
			oneItemWCURs[i]=new WCUL();
			oneItemWCURs[i].setItem(i+1);
		}
		//HT dÃ¹ng Ä‘á»ƒ lÆ°u vá»‹ trÃ­ cá»§a tran trong WCUL cá»§a item cuá»‘i cÃ¹ng
		HashMap<String, Integer> HT=new HashMap<String,Integer>();
		int tid=0;
		
		for(Transaction tran:dataset.transactions)
		{
			//neu giao dá»‹ch khÃ´ng tá»“n táº¡i, bá»� qua luÃ´n
			if(tran.tu==0)
				continue;
			
			tid++;
			int ru=0;
			//neu lÃ  transaction dong
			if(tran.items.length==dataset.maxItem)//close transaction
			{
				int cru=0;
				for(int i=tran.items.length-1; i>=0;i--)
				{
					int item=tran.items[i];
					int cU=tran.utilities[i];
					oneItemWCURs[item-1].CU+=cU;
					oneItemWCURs[item-1].CRU+=cru;
					oneItemWCURs[item-1].CSUP++;
					cru=cru+cU;
				}
				continue;
			}
			
			
			String newT=tran.itemToString();//mÃ£ hÃ³a tran thÃ nh chuoi cÃ¡c item 
			Object dupPos=HT.get(newT);//tim xem cÃ³ chuá»—i váº­y trong Hashtable chÆ°a?
			if(dupPos==null)//neu khÃ´ng cÃ³ thÃ¬ thÃªm dÃ²ng má»›i vÃ o cÃ¡c oneWCUL
			{
				int xk=tran.items[tran.items.length-1];//lay item cuoi trong tran
				int vitri=oneItemWCURs[xk-1].tidList.size();
				HT.put(newT,vitri++);//Ä‘Æ°a chuá»—i ma hÃ³a vÃ  vá»‹ trÃ­ thÃªm vÃ o HT
				
				for(int i=tran.items.length-1; i>=0;i--)
				{
					int pPos=-1;
					int item=tran.items[i];//lay item dang xet
					int nU=tran.utilities[i];
					if(i>0)
						pPos=oneItemWCURs[tran.items[i-1]-1].tidList.size();
					Tid_WCUL tidCur=new Tid_WCUL(tid, nU, ru, 0, pPos);
					//them tidCur vÃ o oneItemWCUL
					oneItemWCURs[item-1].tidList.add(tidCur);
					oneItemWCURs[item-1].NU+=nU;
					oneItemWCURs[item-1].NRU+=ru;
					oneItemWCURs[item-1].NSUP++;
					ru=ru+nU;
				}
			}
			else
			{
				int pos=(int)dupPos;
				int item;
				for(int i=tran.items.length-1;i>=0;i--)
	   			{
					item=tran.items[i];
					int nU=tran.utilities[i];
					try
					{
		   				Tid_WCUL tidcur= oneItemWCURs[item-1].tidList.get(pos);
		   				tidcur.setNU(tidcur.NU+nU);
		   				tidcur.setNRU(tidcur.NRU+ru);
		   				pos=tidcur.getPPOS();
		   				tidcur.Weight++;
		   				oneItemWCURs[item-1].NU+=nU;
						oneItemWCURs[item-1].NRU+=ru;
						oneItemWCURs[item-1].NSUP++;
						ru=ru+nU;
					} catch(Exception e)
					{
						System.out.println("LOi");
					}
	   				
	   			}	   			
			}			
		}
	}
	
	public WCUL[] construct(int k, int item, WCUL[] culs, int pos)
	{		
		int n=culs.length;
		int sz=n-pos; //sz lÃ  sá»‘ lÆ°á»£ng WCUL tá»‘i Ä‘a Ä‘Æ°á»£c sinh ra 
		int exSize=sz; //sÃ´ lÆ°á»£ng exWCUL hiá»‡n táº¡i
		
		WCUL[] exCULs = new WCUL[sz];
		WCUL cul=culs[pos-1]; //WCUL gá»‘c
		
		//tham sá»‘ dÃ¹ng Ä‘á»ƒ thá»±c hiá»‡n cÃ¡c chiáº¿n lÆ°á»£c tá»‰a
		int[] prunLAU=new int[sz]; //tá»‰a LA
		int[] prunCUTIL=new int[sz]; //tá»‰a C
		int[] ey=new int[sz];

		float[] prunCOR=new float[sz]; //tá»‰a LA-kulc
		int [] supWCUL=new int[sz];//luu sup toi da cá»§a cac exWcul
		int [] minSupWCUL=new int[sz];//so lung supp toi thieu
		
		float tempCor=minCor*(k+1);
		float cu=(cul.COR*k)/(cul.NSUP+cul.CSUP);
			
		
		int supXx=cul.CSUP + cul.NSUP;//do ho tro toi da chung cho táº¥t cáº£ cÃ¡c exWCUL
		float tamCor=(cul.COR*k)/supXx;
		
		
		//tia EUCP
		for(int i=0;i<sz;i++)
		{
			WCUL cur_cul=culs[pos+i];
			int cur_item=cur_cul.item;
						
			if(EUCS[item][cur_item]<minUtil)
			{
//				System.out.println("tia EUCS: " + item + " " + cur_item);
				exCULs[i]=null;
				exSize--;
				continue;
			}
			
			
			
			WCUL newCUL=new WCUL();
			
			
			//float tam=(tamCor + 1.0f/supports[cur_item])/(k+1);
			float tam=(tamCor + 1.0f/supps[cur_item])/(k+1);
			//supps
			
			newCUL.item=cur_item;
			newCUL.CU=cul.CU + cur_cul.CU-cul.CPU;
			newCUL.CRU=cur_cul.CRU;
			newCUL.CPU=cul.CU;//dang che
			newCUL.COR=tam;//cor khi thÃªm hay bÆ¡t má»™t transaction
			newCUL.CSUP=cul.CSUP;
			exCULs[i]=newCUL;
			
						
			ey[i]=0;
			prunLAU[i]=cul.CU+ cul.CRU+cul.NRU+cul.NU; //u+ru tá»‘i Ä‘a cho wcul thá»© i
			prunCUTIL[i]=cul.CU+cul.CRU; //u + ru tá»‘i thiá»ƒu cho wcul thá»© i
			
			prunCOR[i]=tam*supXx;//cor tá»‘i Ä‘a
			supWCUL[i]=supXx; // Ä‘á»™ há»— trá»£ tá»‘i Ä‘a cho wcul thÆ° i
			minSupWCUL[i]=(int)(0.5 + tempCor/(cu+1.0/supports[cur_item]));
	
			//LA-KCUL
//			if(supWCUL[i]<minSupWCUL[i])
//			{
//				exSize--;
//				exCULs[i]=null;
//			}
			//End LA-kcul
			
		}
		//giáº£m Ä‘i Lau cho vi má»™t sá»‘ exWCUL bá»‹ null do EUCS 
		int Unull=0;
		for(int i=sz-1;i>=0;i--)
		{
			if(exCULs[i]==null)
			{
				Unull+=culs[pos+i].CU-culs[pos+i].CPU;
			}
			else
			{
				exCULs[i].CRU-=Unull;
				prunLAU[i]-=Unull;
			}
		}
		
		
		
		
		HashMap<String, Integer> HT=new HashMap<String, Integer>();
		for(Tid_WCUL tidcul:cul.tidList)
		{
			int tid=tidcul.Tid;
			int dem=0;
			int cuoi=0;
			int[] listItemHas=new int[exSize];
			StringBuffer listItem=new StringBuffer();
			for(int i=0;i<sz;i++)
			{
				if(exCULs[i]==null)
					continue;
				WCUL cur_cul=culs[pos+i];
				int size=culs[pos+i].tidList.size();
				while(ey[i]<size && cur_cul.tidList.get(ey[i]).Tid<tid)
					ey[i]++;
				if(ey[i]<size && cur_cul.tidList.get(ey[i]).Tid==tid)//tid nay cÃ³ trong cur_cul
				{
					listItem.append(i + " ");
					cuoi=i;	
					listItemHas[dem]=i;
					dem++;
				}
				else //tid khÃ´ng cÃ³ trong cur_cul
				{
					//giam Ä‘i LAU tá»‘i Ä‘a
					prunLAU[i]-=tidcul.NU-tidcul.NRU;
					supWCUL[i]-=tidcul.Weight;
					int Uxoa=exCULs[i].CU-exCULs[i].CPU;//gia tri LAU giam náº¿u exWCUL bá»‹ há»§y
					//neu sau khi cáº­p nháº­t prunLau khong thá»�a minUtil thÃ¬ loáº¡i bo
//					if(prunLAU[i]<minUtil || supWCUL[i]<minSupWCUL [i])
					if(prunLAU[i]<minUtil)
					{
						//System.out.println("tia LA prunce ");
						//int Uxoa=exCULs[i].CU-exCULs[i].CPU;
						//System.out.println("La ne" + LAU[j]);
						exCULs[i]=null;//LA-prune
						exSize--;
						//cap nhat LAU cá»§a cac CUL
						for(int bi=0;bi<i;bi++)
						{
							if(exCULs[bi]!=null)
							{
								exCULs[bi].CRU-=Uxoa;
								prunLAU[bi]-=Uxoa;
							}
						}
						continue;
					}
				//	prunCOR[i]-=tidcul.Weight*exCULs[i].COR;	
				}			
			}
			//da kiem tra xong xem tid chua o cur nÃ o
			if(dem==exSize)//day lÃ  tid dong
			{
				//them vao phan du lieu dong
				int nru=0;
				for(int i=sz-1;i>=0;i--)
				{
					if(exCULs[i]==null)
						continue;
					WCUL cur_cul=culs[pos+i];					
					int cur_cul_NU=cur_cul.tidList.get(ey[i]).NU;
					exCULs[i].CU+=tidcul.NU + cur_cul_NU-tidcul.PU;
					exCULs[i].CRU+=nru;
					exCULs[i].CPU+=tidcul.NU;
					nru=nru+cur_cul_NU-tidcul.PU;
					exCULs[i].CSUP+=tidcul.Weight;
				}
			}
			else//khong phai tid dong
			{
				Integer posObject=HT.get(listItem.toString());
				if(posObject==null)//chua cÃ³ cÃ¡i nÃ o giá»‘ng Ä‘á»ƒ merge
				{
					//Them mot tidCUL má»›i vao CUL
					try
					{
						HT.put(listItem.toString(), exCULs[cuoi].tidList.size());
					}
					catch(NullPointerException e)
					{
						//HT.put(listItem.toString(), exCULs[cuoi].tidList.size());
					}
					int NRU=0;
					for(int j=dem-1;j>=0;j--)
					{
						//int vvvt=Integer.parseInt(ds[j]);
						int vt=listItemHas[j];
						WCUL culht=culs[pos+vt];
						Tid_WCUL tidCULht=culht.tidList.get(ey[vt]);
												
						int NPU=tidcul.PU;//bá»Ÿi vi khong Ä‘Ã³ng					
						int addtid=tidcul.Tid;
						int addnU=tidcul.NU + tidCULht.NU-NPU;
						int addnRU=NRU;
						int addpU=tidcul.NU;
						int addpPOS=-1;
						if(j>0)
						{
							addpPOS=exCULs[listItemHas[j-1]].tidList.size();
						}
						Tid_WCUL tidCULAdd=new Tid_WCUL(addtid, addnU, addnRU, addpU, addpPOS);
						tidCULAdd.Weight=tidcul.Weight;
						
						WCUL dangxuly =exCULs[vt];
						dangxuly.tidList.add(tidCULAdd);
						//Cap nhat láº¡i cÃ¡c thÃ´ng tin cá»§a dangxuly (NU, NRU, CU/CRU/CPU)
						dangxuly.NU+=addnU;
						dangxuly.NRU+=addnRU;
						dangxuly.NSUP+=tidcul.Weight;
						NRU+=tidCULht.NU-NPU;
						//CRU vÃ  CPU, CU khong Ä‘á»•i vÃ¬ X khong dong
					}
				}
				else //co mot tran giá»‘ng
				{
					int vitri=(int)posObject;
					//Cap nhat tidCUL táº¡i vi tri Ä‘Ã£ xÃ¡c Ä‘á»‹nh
					int NRU=0;
					for(int j=dem-1;j>=0;j--)
					{
						int vt=listItemHas[j];
						WCUL culht=culs[pos+vt];
						WCUL dangxuly=exCULs[vt];
						Tid_WCUL update= dangxuly.tidList.get(vitri);
						
						///
						int NPU=tidcul.PU;
						int NUht=culht.tidList.get(ey[vt]).NU;
						update.NU+=tidcul.NU+NUht -NPU;
						update.NRU+=NRU;
						update.PU+=tidcul.NU;
						update.Weight+=tidcul.Weight;
								
						dangxuly.NU+=tidcul.NU+NUht -NPU;
						dangxuly.NRU+=NRU;
						dangxuly.NSUP+=tidcul.Weight;
						
						NRU=NRU+NUht -NPU;
						vitri=update.PPOS;
					}
				}
			}
			
			int them=tidcul.NU + tidcul.NRU;
			for(int i=0;i<sz;i++)
				if(exCULs[i]!=null)
					prunCUTIL[i]+=them;
		}
		for(int i=0;i<sz;i++)
		{
			if(prunCUTIL[i]<minUtil && exCULs[i]!=null)//de kiem tra Ä‘Ã£ bá»‹ 
			{
				exCULs[i]=null;
				exSize--;
			}
		}
		
		WCUL[] kq;
		if(exSize!=0)
		{
			kq=new WCUL[exSize];
			int pt=0;
			for(int j=0;j<sz;j++)
				if(exCULs[j]!=null)
				{
					kq[pt]=exCULs[j];
					//kq[pt].COR=prunCOR[j];
					kq[pt].COR=(kq[pt].CSUP +kq[pt].NSUP)*kq[pt].COR;
					pt++;
				}
			return kq;	
		}
		else
			return null;
		
		
	}
	public void printLog(String output, String dbName) throws IOException 
	{
		BufferedWriter writerlog = new BufferedWriter(new FileWriter(output,true));
		writerlog.newLine();
		writerlog.write("========== HMinerCo ============"); writerlog.newLine();
		writerlog.write(" Dataset = " + dbName);writerlog.newLine();
		writerlog.write(" minUtil = " + this.minUtil);writerlog.newLine();
		writerlog.write(" Mincor = " + this.minCor);writerlog.newLine();
		writerlog.write(" Number of Trans = " + this.SoTran);writerlog.newLine();
		
		writerlog.write(" Number of Cohuis= " + this.lstCoHUIs.size());writerlog.newLine();
		writerlog.write(" Number of Candidate = " + this.candidateCount);writerlog.newLine();
		writerlog.write(" Times (ms)= " + this.times);writerlog.newLine();
		
		writerlog.write(" Max memory:" + G1Total);writerlog.newLine();
		writerlog.write("=====================================");
		writerlog.close();
		System.out.println("Finish");
	}
	
	
	
	
	public static void printPeakHeapUsage()
    {
    	try {
            List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
            // we print the result in the console
			double total = 0;
			for (MemoryPoolMXBean memoryPoolMXBean : pools) {
				if (memoryPoolMXBean.getType() == MemoryType.HEAP) {
					long peakUsed = memoryPoolMXBean.getPeakUsage().getUsed();
					System.out.println(String.format("Peak used for: %s is %.2f", memoryPoolMXBean.getName(), (double)peakUsed/1024/1024));
					total = total + peakUsed;
				}
			}
			System.out.println(String.format("Total heap peak used: %f MB", total/1024/1024));
			G1Total=(int) (total/1024/1024);
     
       } catch (Throwable t) {
            System.err.println("Exception in agent: " + t);
       }
    }

}
