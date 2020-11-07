package algorithmHT2020;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
public class addNamesToVisciousFingers {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		String names = readNTG.readFromFile("vfNames.ntg");
		List<String>[] info = findNamesAndIds(names);
		
		String noNames = readNTG.readFromFile("vf40to47_version1.ntg");
		String namesAdded = addNamesToIds(noNames, info);
		
		toNTG(namesAdded, "visciousFingers.ntg");

	}
	
	public static void toNTG(String namesAdded, String fileName) throws IOException{
		String path = new File("").getAbsolutePath() + "\\datasets\\temporaltrees\\" + fileName;
		try (PrintWriter writer = new PrintWriter(new File(path))) {
			  StringBuilder sb = new StringBuilder();
			  sb.append(namesAdded);
			  writer.write(sb.toString());
		}
		catch (FileNotFoundException e) {
			 System.out.println(e.getMessage());
		}

	}
	public static String addNamesToIds(String noNames, List<String>[] info) {
		String noEdit = noNames.substring(0, noNames.indexOf("\"N\"")+5);
		String edit = noNames.substring(noNames.indexOf("\"N\"")+5, noNames.length() - 1);
		while (edit.length()>0) {
			int t_index = edit.indexOf("\"t\"");
			int closing_index = edit.substring(t_index, edit.length()-1).indexOf("}") + 1;
			String node =  edit.substring(0,t_index+closing_index);
			edit = edit.substring(t_index + closing_index+1, edit.length());
			String id = node.substring(1, node.indexOf(":")-1);
			String nameAdded = "\"" + info[0].get(info[1].indexOf(id)) + "\"" +  node.substring(node.indexOf(":"), node.length()) + ",";
			noEdit += nameAdded;
		}
		noEdit = noEdit.substring(0,noEdit.length()-1) + "}}";
		return noEdit;
		
	}
	public static List<String>[] findNamesAndIds(String s) {
		List<String>[] info = new List[2];
		List<String> names = new ArrayList<>();
		List<String> ids = new ArrayList<>();
		info[0] = names;
		info[1] = ids;
		String hardCoded = s.substring(s.indexOf("\"N\"")+5, s.length() - 1);
		int sum = 0;
		while (hardCoded.length()>0) {
			int t_index = hardCoded.indexOf("\"t\"");
			int closing_index = hardCoded.substring(t_index, hardCoded.length()-1).indexOf("}") + 1;
			String node =  hardCoded.substring(0,t_index+closing_index);
			hardCoded = hardCoded.substring(t_index + closing_index+1, hardCoded.length());
			
			String name = node.substring(1,node.indexOf(":")-1);
			info[0].add(name);
			info[1].add(name.split("_")[0]);
		}
		return info;
	}

}
