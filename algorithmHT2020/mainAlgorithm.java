package algorithmHT2020;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class mainAlgorithm {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = readNTG.generateNodes("cousinsTriple.ntg");
//		for (String key : nodes.keySet()) {
//			System.out.println(nodes.get(key).getValue());
//		}
		List<List<Integer>>[] order = orderNodes.getOrders(nodes);
		
		nodes = reformatData.recalculateParentData(nodes);
		order = reformatData.configureParentSwaps(nodes, order);
		order = reformatData.configureNameSwaps(nodes, order);
//		for (int i = 0; i < order.length; i++) {
//			System.out.println(i + " " + order[i]);
//		}
//			
		toCSV(nodes, order,"viciousFingers.csv");
		//generateReferenceCSVs("europeTest.csv", 10);
		
	}
	
	
	public static void toCSV (HashMap<String, myNode> nodes, List<List<Integer>>[] order, String fileLocation) {
		try (PrintWriter writer = new PrintWriter(new File(fileLocation))) {
			  StringBuilder sb = new StringBuilder();
			  sb.append("Testing");
			  sb.append(",");
			  sb.append("Parent");
			  sb.append(",");
			  Integer[] minMaxTime = aggregatedNodes.computeTimespan(nodes);
			  String timespan = "";
			  for (int i = minMaxTime[0]; i <= minMaxTime[1]; i++) {
				  if(i == minMaxTime[1]) {
					  timespan += Integer.toString(i);  
				  }
				  else {
					  timespan += Integer.toString(i) + ",";  
				  }
			  }
			  sb.append(timespan);
			  sb.append('\n');
			  
			  for (int i = 0; i < order.length; i++) {
				  for (int j = 0; j < order[i].size(); j++) {
					  sb.append(nodes.get(Integer.toString(order[i].get(j).get(0))).name);
					  sb.append(",");
					  String parent;
					  if (nodes.get(Integer.toString(order[i].get(j).get(0))).parent == 0) parent = "root";
					  else parent = nodes.get(Integer.toString(nodes.get(Integer.toString(order[i].get(j).get(0))).parent)).name;
					  sb.append(parent);
					  sb.append(",");

					  int timeDiff = minMaxTime[1] - minMaxTime[0] +1 ;
					  Double[] dataSeries = new Double[timeDiff];
					  Arrays.fill(dataSeries, 0.0d);
					  for (int k = 0; k < order[i].get(j).size(); k++) {
						  int idx = nodes.get(Integer.toString(order[i].get(j).get(k))).time - minMaxTime[0];
						  dataSeries[idx] = nodes.get(Integer.toString(order[i].get(j).get(k))).size;
					  }
					  
					  String data = Arrays.toString(dataSeries);
					  data = data.replace(" ", "");
					  data = data.replace("[", "");
					  data = data.replace("]", "");
	
					  sb.append(data);
					  sb.append("\n");
				  }
			  }
			  writer.write(sb.toString());
			  } catch (FileNotFoundException e) {
				  System.out.println(e.getMessage());
			  }
		try {
			String test = outputTest.testOrder(nodes, fileLocation);
			System.out.println(test);
		} catch (IOException e) {
			System.out.println("test failed IO error");
		}
	}
	
	public static void generateReferenceCSVs(String fileName, int n) throws IOException {
		String path = new File("").getAbsolutePath() + "\\" + fileName;
		File file = new File(path);
		Scanner input = new Scanner(file);
		String header = input.nextLine();
		List<String> nodes = new ArrayList<>();
		while (input.hasNextLine()) {
		    nodes.add(input.nextLine());
		}
		String[] fileNameSplit = fileName.split("\\.");
		String saveTo = new File("").getAbsolutePath() + "\\referenceCSVsHT\\" + fileNameSplit[0];
		System.out.println(saveTo);
		for (int i = 0; i < n; i++) {
			String referenceName = fileNameSplit[0] + (i+1) + "." + fileNameSplit[1];
			System.out.println(referenceName);
			try (PrintWriter writer = new PrintWriter(new File(saveTo, referenceName))) {
				  StringBuilder sb = new StringBuilder();
				  sb.append(header);
				  sb.append("\n");
				  
				  Collections.shuffle(nodes);
				  //System.out.println(nodes);
				  for (int j = 0; j < nodes.size(); j++) {
					  sb.append(nodes.get(j));
					  sb.append("\n");
				  }
				  writer.write(sb.toString());
			}
			catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			}
		}
		
	}
}
