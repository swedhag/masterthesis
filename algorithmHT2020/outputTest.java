package algorithmHT2020;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class outputTest {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = readNTG.generateNodes("codeRepo.ntg");
		List<List<Integer>>[] order = orderNodes.getOrders(nodes);
		nodes = reformatData.recalculateParentData(nodes);
		order = reformatData.configureParentSwaps(nodes, order);
		order = reformatData.configureNameSwaps(nodes, order);
		
		outputTest.testOrder(nodes, "testing.csv");
	}

	public static String testOrder(HashMap<String, myNode> nodes, String fileLocation) throws IOException {
		TreeMap<Integer, List<String>> csvOrder = orderFromCSV(nodes, fileLocation);
		TreeMap<Integer, List<String>> ntgOrder = orderFromNTG(nodes);
		
		Boolean testPassed = true;
		for (Integer key : csvOrder.keySet()) {
			if ( !(csvOrder.get(key).equals(ntgOrder.get(key))) ) {
				testPassed = false;
				System.out.println(key + ": (CSV)" + csvOrder.get(key));
				System.out.println(key + ": (NTG)" + ntgOrder.get(key));
				break;
			}

		}
		
		if (testPassed == true) return "CSV output file have aggregated nodes ordered correctly";
		else return "Order test failed";
		
	}
	
	public static TreeMap<Integer, List<String>> orderFromCSV(HashMap<String, myNode> nodes, String fileLocation) throws IOException {
		Integer[] minMaxTime = aggregatedNodes.computeTimespan(nodes);
		String path = new File("").getAbsolutePath() + "\\" + fileLocation;
		File file = new File(path);
		Scanner input = new Scanner(file);
		String template = input.nextLine();
		String[] templateArr = template.split(",");
		List<String[]> lineArrays = new ArrayList<>();
		while (input.hasNextLine()) {
			String line = input.nextLine();
			String[] lineArr = line.split(",");
			lineArrays.add(lineArr);
		}

		TreeMap<Integer, List<String>> csvOrder = new TreeMap<>();
		for (int i = 2; i < templateArr.length; i++) {
			int time = Integer.parseInt(templateArr[i]);
			for (int j = 0; j < lineArrays.size(); j++) {
				if (Double.parseDouble(lineArrays.get(j)[i]) != 0.0d) {
					if (csvOrder.containsKey(time)) {
						csvOrder.get(time).add(lineArrays.get(j)[0]);
					}
					else {
						List<String> order = new ArrayList<>();
						order.add(lineArrays.get(j)[0]);
						csvOrder.put(time, order);
					}
				}
			}
		}
		return csvOrder;
	}
	
	public static TreeMap<Integer, List<String>> orderFromNTG(HashMap<String, myNode> nodes) {
		int[] maxPositions = orderNodes.maxPositionsByLayer(nodes);
		Integer[] minMaxTime = aggregatedNodes.computeTimespan(nodes);
		TreeMap<Integer, List<String>> ntgOrder = new TreeMap<>();
		for (int i = minMaxTime[0]; i <= minMaxTime[1]; i++) {
			List<String> order = new ArrayList<>();
			ntgOrder.put(i, order);
		}
		for (int i = 0; i < maxPositions.length; i++) {
			for (int j = minMaxTime[0]; j <= minMaxTime[1]; j++) {
				List<Integer> positions = new ArrayList<>();
				List<String> names = new ArrayList<>();
				for (String key : nodes.keySet()) {
					myNode node = nodes.get(key);
					if ( (node.layer == i) && (node.time == j)) {  
						positions.add(node.position);
						names.add(node.name);	
					}
				}
				String[] orderArr = new String[positions.size()];
				for (int k = 0; k < positions.size(); k++) {
					orderArr[positions.get(k)] = names.get(k);
				}
				List<String> order = new ArrayList<>();
				for (int k = 0; k < orderArr.length; k++) {
					order.add(orderArr[k]);
				}
				for (int k = 0; k < order.size(); k++) {
					ntgOrder.get(j).add(order.get(k));
				}
			}
		}
		return ntgOrder;
	}
}
