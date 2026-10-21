package AlgCoHUI_Miner;

import java.util.ArrayList;
import java.util.List;

public class WCUL {
	
	int item;
	int NU;
	int NRU;
	int CU;
	int CRU;
	int CPU;
	int NSUP, CSUP;
	float COR;
	
	List<Tid_WCUL> tidList;
	
	public WCUL()
	{
		tidList=new ArrayList<Tid_WCUL>();
		NU=0;
		NRU=0;
		CU=0;
		CRU=0;
		CPU=0;
		CSUP=0;
		NSUP=0;
		COR=1;
	}
	
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public int getNU() {
		return NU;
	}
	public void setNU(int nU) {
		NU = nU;
	}
	public int getNRU() {
		return NRU;
	}
	public void setNRU(int nRU) {
		NRU = nRU;
	}
	public int getCU() {
		return CU;
	}
	public void setCU(int cU) {
		CU = cU;
	}
	public int getCRU() {
		return CRU;
	}
	public void setCRU(int cRU) {
		CRU = cRU;
	}
	public int getCPU() {
		return CPU;
	}
	public void setCPU(int cPU) {
		CPU = cPU;
	}
	public List<Tid_WCUL> getTidList() {
		return tidList;
	}
	public void setTidList(List<Tid_WCUL> tidList) {
		this.tidList = tidList;
	}
	public int getNPU()
	{
		int tong=0;
		for(Tid_WCUL tid:tidList)
			tong+=tid.PU;
		return tong;
	}

	@Override
	public String toString() {
		StringBuffer str=new StringBuffer();
		str.append("CUL [item=" + item + ", NU=" + NU + ", NRU=" + NRU + ", CU=" + CU + ", CRU=" + CRU + ", CPU=" + CPU
				+", CSup=" + (CSUP +"  NSUP: "+NSUP)+ ", COR=" + COR +"]");
		str.append(System.getProperty("line.separator"));
		for(Tid_WCUL tid:tidList)
		{
			str.append(tid.toString());
			str.append(System.getProperty("line.separator"));
		}
		return str.toString();
	}

	public int getNSUP() {
		return NSUP;
	}

	public void setNSUP(int nSUP) {
		NSUP = nSUP;
	}

	public int getCSUP() {
		return CSUP;
	}

	public void setCSUP(int cSUP) {
		CSUP = cSUP;
	}
	
	public float getCOR() {
		return COR;
	}

	public void setCOR(float cCOR) {
		COR = cCOR;
	}

}
