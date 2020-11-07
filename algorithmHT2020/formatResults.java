package algorithmHT2020;

import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class formatResults {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String toDir = "cousinsTriple";
		String fileName = "cousinsTripleSNDMod";
		String s = "	newTime = 1	1.0\r\n" + 
				"0.0\r\n" + 
				"	newTime = 2	1.0\r\n" + 
				"0.0\r\n" + 
				"	newTime = 3	1.0\r\n" + 
				"0.0\r\n" + 
				"	newTime = 4	0.7643097643097642\r\n" + 
				"146.6158343664541\r\n" + 
				"	newTime = 5	1.0\r\n" + 
				"0.0\r\n" + 
				"	newTime = 6	0.7589285714285716\r\n" + 
				"139.32122442517226\r\n" + 
				"	newTime = 7	1.0\r\n" + 
				"0.0\r\n" + 
				"	newTime = 8	1.0\r\n" + 
				"0.0";
		List<String> stabilities = stabilities(s);
		saveStabilities(toDir, fileName, stabilities);
	}
	
	public static void saveStabilities(String toDir, String fileName, List<String> stabilities) {
		String saveTo = new File("").getAbsolutePath() + "\\results\\" + toDir;
		try (PrintWriter writer = new PrintWriter(new File(saveTo, fileName))) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < stabilities.size(); i++) {
				sb.append(stabilities.get(i));
				sb.append("\n");
			}
			writer.write(sb.toString());
		}
		catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static List<String> stabilities(String s) {
		List<String> stabilities = new ArrayList<>();
		stabilities.add("Time,RelativeQuadrantStability,LayoutDistanceChange");
		String[] sArr = s.split("\\s");
		int cnt = 0;
		for (int i = 0; i < sArr.length; i++) {
			if( sArr[i].contentEquals("=") ) {
				cnt++;
				stabilities.add(cnt +"," + sArr[i+2]+","+sArr[i+4]);
			}
		}
		return stabilities;
	}
	
	
}
