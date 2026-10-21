package AlgCoHUI_Miner;

public class Tid_WCUL {
	int Tid;
	int NU;
	int NRU;
	int PU;
	int PPOS;
	int Weight;
	
	public int getTid() {
		return Tid;
	}
	public void setTid(int tid) {
		this.Tid = tid;
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
	public int getPU() {
		return PU;
	}
	public void setPU(int pU) {
		PU = pU;
	}
	public int getPPOS() {
		return PPOS;
	}
	public void setPPOS(int pPOS) {
		PPOS = pPOS;
	}
	public Tid_WCUL(int tid, int nU, int nRU, int pU, int pPOS) {
		super();
		this.Tid = tid;
		NU = nU;
		NRU = nRU;
		PU = pU;
		PPOS = pPOS;
		Weight=1;
	}
	@Override
	public String toString() {
		return "TidCUL [" + Tid + ", " + NU + ", " + NRU + ", " + PU + ", " + PPOS + ", " + Weight +"]";
	}
}
