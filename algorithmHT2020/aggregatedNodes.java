package algorithmHT2020;

import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class aggregatedNodes {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = readNTG.generateNodes("europeWTF.ntg");
		List<List<Integer>> evolvements = aggregate(nodes);
		//System.out.println(evolvements.get(0).get(0));
//		for (int i = 0; i < evolvements.size(); i++) {
//			String lol = "";
//			for (int j = 0 ; j < evolvements.get(i).size(); j++) {
//				lol += "->" + nodes.get(Integer.toString(evolvements.get(i).get(j))).name;
//			}
//			System.out.println(lol);
//		}
	}

	
	public static List<List<Integer>> aggregate(HashMap<String, myNode> nodes){
		Integer[] timespan = computeTimespan(nodes);
		List<List<Integer>> evolvements = new ArrayList<>();
		for (String key : nodes.keySet()) {
			if( nodes.get(key).time == timespan[0]) {
				List<Integer> startingNode = new ArrayList<>();
				startingNode.add(nodes.get(key).id);
				//System.out.println(nodes.get(key).name);
				evolvements.add(startingNode);
			}
		}
		
		List<Integer> duplicateHandling = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			Boolean finished = false;
			while (finished == false) {
				List<Integer> becomes = findNext(nodes, evolvements.get(i));
				if(becomes.size() == 0) {
					finished = true;
				}
				else if (becomes.size() == 1) {
					int currentID = evolvements.get(i).get(evolvements.get(i).size()-1);
					String currentName = nodes.get(Integer.toString(currentID)).name;
					String nextName = nodes.get(Integer.toString(becomes.get(0))).name;
					//Regular
					if ( currentName.equals(nextName) ) {
						evolvements.get(i).add(becomes.get(0));
						finished = false;
					}
					else {
						if ( !(duplicateHandling.contains(becomes.get(0))) ) {
							List<Integer> nodeChange = new ArrayList<>();
							nodeChange.add(becomes.get(0));
							evolvements.add(nodeChange);
							duplicateHandling.add(becomes.get(0));
						}
						finished = true;
					}
				}
				else if (becomes.size() > 1) {
					for (int j = 0; j < becomes.size(); j++) {
						if ( !(duplicateHandling.contains(becomes.get(j))) ) {
							List<Integer> split = new ArrayList<>();
							split.add(becomes.get(j));
							evolvements.add(split);
							duplicateHandling.add(becomes.get(j));
						}
					}
					finished = true;
				}
			}
		}
		return evolvements;
	}
	public static List<Integer> findNext (HashMap<String, myNode> nodes, List<Integer> evolvingNode) {
		List<Integer> becomes = nodes.get(Integer.toString(evolvingNode.get(evolvingNode.size()-1))).becomes;
		return becomes;
	}
	public static Integer[] computeTimespan (HashMap<String, myNode> nodes) {
		Integer[] timespan = new Integer[2];
		Boolean first = true;
		for (String key : nodes.keySet()) {
			if( first == true) {
				timespan[0] = nodes.get(key).time;
				timespan[1] = nodes.get(key).time;
				first = false;
			}
			else {
				if (timespan[0] > nodes.get(key).time) timespan[0] = nodes.get(key).time;
				else if (timespan[1] < nodes.get(key).time) timespan[1] = nodes.get(key).time;
			}
		}
		return timespan;
	}
		
	public static List<List<String>> getNames(HashMap<String, myNode> nodes,List<List<Integer>> evolvements) { 
		List<List<String>> names = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			List<String> nodeName = new ArrayList<>();
			for (int j = 0; j < evolvements.get(i).size(); j++) {
				String name = nodes.get(Integer.toString(evolvements.get(i).get(j))).name;
				nodeName.add(name);
			}
			names.add(nodeName);
		}
		return names;
	}
	
	public static List<List<Double>> getSizes(HashMap<String, myNode> nodes,List<List<Integer>> evolvements) { 
		List<List<Double>> sizes = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			List<Double> nodeSize = new ArrayList<>();
			for (int j = 0; j < evolvements.get(i).size(); j++) {
				Double size = nodes.get(Integer.toString(evolvements.get(i).get(j))).size;
				nodeSize.add(size);
			}
			sizes.add(nodeSize);
		}
		return sizes;
	}
	
	public static List<List<Integer>> getPositions(HashMap<String, myNode> nodes,List<List<Integer>> evolvements) { 
		List<List<Integer>> positions = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			List<Integer> nodePosition = new ArrayList<>();
			for (int j = 0; j < evolvements.get(i).size(); j++) {
				Integer position = nodes.get(Integer.toString(evolvements.get(i).get(j))).position;
				nodePosition.add(position);
			}
			positions.add(nodePosition);
		}
		return positions;
	}
	public static Object[] getNamesSizesPositionsTimes(HashMap<String, myNode> nodes,List<List<Integer>> evolvements) {
		List<List<Double>> sizes = new ArrayList<>();
		List<List<String>> names = new ArrayList<>();
		List<List<Integer>> positions = new ArrayList<>();
		List<List<Integer>> times = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			List<Double> nodeSize = new ArrayList<>();
			List<String> nodeName = new ArrayList<>();
			List<Integer> nodePosition = new ArrayList<>();
			List<Integer> nodeTime = new ArrayList<>();
			for (int j = 0; j < evolvements.get(i).size(); j++) {
				Double size = nodes.get(Integer.toString(evolvements.get(i).get(j))).size;
				String name = nodes.get(Integer.toString(evolvements.get(i).get(j))).name;
				Integer position = nodes.get(Integer.toString(evolvements.get(i).get(j))).position;
				Integer time = nodes.get(Integer.toString(evolvements.get(i).get(j))).time;
				nodeSize.add(size);
				nodeName.add(name);
				nodePosition.add(position);
				nodeTime.add(time);
			}
			sizes.add(nodeSize);
			names.add(nodeName);
			positions.add(nodePosition);
			times.add(nodeTime);
		}
		return new Object[] {names, sizes, positions, times};
	}
	
	public static List<Integer> getStartingIDs(List<List<Integer>> evolvements) {
		List<Integer> startingIDs = new ArrayList<>();
		for (int i = 0; i < evolvements.size(); i++) {
			startingIDs.add(evolvements.get(i).get(0));
		}
		return startingIDs;
	}
}
