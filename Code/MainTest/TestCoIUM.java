package MainTest;

import java.io.IOException;
import AlgCoIUM.*;

public class TestCoIUM {
	public static void main(String [] arg) throws IOException{	
		
		String[] db= { "chess_utility_spmf.txt", //0
				"connect_utility_spmf.txt",//1
				"accidents_utility_spmf.txt",//2
				"mushroom_utility_SPMF.txt",//3
				"chainstore.txt",//4
				"kosarak_utility_spmf.txt",//5
				"dbtest.txt",//6
				"T10I4N4KD5000K.txt"//7
		};

		String inputPath=db[1];
		
		String path="E:\\Vinhvv\\Hufi\\NghienCuuKhoaHoc\\Top_K\\DeTaiCapTruong\\CoHUI";
		String input = "E:\\Vinhvv\\Hufi\\NghienCuuKhoaHoc\\Top_K\\DeTaiCapTruong\\CoHUI\\bin\\MainTest\\" + inputPath;
				
		System.out.println("DB: " + input.substring(input.lastIndexOf("\\")+1));
		
		//for(int i=0;i<10;i++) {
		CoIUM algo = new CoIUM();
		int minUtil=40;
		double minCor=0.4;		
		int soLuong =Integer.MAX_VALUE;
		
		minUtil=Integer.parseInt(arg[0]);
		minCor=Double.parseDouble(arg[1]);		
		soLuong =Integer.parseInt(arg[2]);
		
		
		algo.runAlgorithm(minUtil,minCor, input, null, true, soLuong);		
		algo.printStats();
		algo.printLog("CoUIM1708_Connect.txt", "Connect");
		//}
	}

}
