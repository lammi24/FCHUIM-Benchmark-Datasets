package MainTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import AlgCoHUI_Miner.HMinerCo;




public class Test_HMinerCo {
	public static void main(String[] args) throws IOException {
		
		
		String[] db= { "chess_utility_spmf.txt", //0
						"connect_utility_spmf.txt",//1
						"accidents_utility_spmf.txt",//2
						"mushroom_utility_SPMF.txt",//3
						"chainstore.txt",//4
						"kosarak_utility_spmf.txt",//5
						"dbtest.txt",//6
						"T10I4N4KD5000K.txt"//7
		};
		
		String inputPath=fileToPath(db[4]);
		System.out.println(inputPath);
		
		
		int minUtil=50000;
		float minCor=0.15f;//.48f;//0.5f;
		int soTran=1000000;
		
		minUtil=Integer.parseInt(args[0]);
		minCor=Float.parseFloat(args[1]);
		soTran=Integer.parseInt(args[2]);
		
		
//		int[] utils= {1900000,1600000,1300000,1000000, 700000};
//		float[] cors= {0.2f, 0.15f, 0.1f};
//		
//		for(int NewminUtil:utils)
//		{
//			for(float NewminCor:cors)
//			{
//				System.out.println("minUtil: " + NewminUtil + " minCor: " + NewminCor);
//				HMinerCo algo=new HMinerCo();
//				algo.runAglo(inputPath, NewminUtil, NewminCor);
//				algo.printPeakHeapUsage();
//			}
//		}
		for(int i=0;i<1;i++)
		{
		HMinerCo algo=new HMinerCo();
		algo.runAglo(inputPath, minUtil, minCor,soTran);
		algo.printPeakHeapUsage();
		algo.printLog("Chainstore1708.txt", "Chainstore");
		}
	}
	public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url= Test_HMinerCo.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}


}
