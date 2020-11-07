package algorithmHT2020;

import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;

//Needed in order to feed the ordered, aggregated nodes, with the temporal
//information added, to Sondag et al.'s software for animating trees.
public class reformatData {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = readNTG.generateNodes("cousins.ntg");
		List<List<Integer>>[] order = orderNodes.getOrders(nodes);

		nodes = recalculateParentData(nodes);
		order = configureParentSwaps(nodes, order);
		order = configureNameSwaps(nodes, order);
		
	}
	
	//Nodes HashMap and order Array as input. If an aggregated node changes
	//its parent, the aggregated node is split into two at the parent swap
	//intersection. Nodes with the new parent have String "New" added to their name.
	//Returns a new order Array with parent swaps configured.
	public static List<List<Integer>>[] configureParentSwaps(HashMap<String, myNode> nodes, List<List<Integer>>[] order) {
		for (int i = 0; i < order.length; i++) {
			for (int j = 0; j < order[i].size(); j++) {
				for (int k = 0; k < order[i].get(j).size() - 1; k++) {
					int currentId = order[i].get(j).get(k);
					int nextId = order[i].get(j).get(k+1);
					int currentParentId = nodes.get(Integer.toString(currentId)).parent;
					int nextParentId = nodes.get(Integer.toString(nextId)).parent;
					if (currentParentId != 0 && nextParentId != 0) { 
						String currentParent = nodes.get(Integer.toString(currentParentId)).name;
						String nextParent = nodes.get(Integer.toString(nextParentId)).name;
						if ( !(currentParent.contentEquals(nextParent)) ) {
							List<List<Integer>> partitioned = splitListAtIdx(order[i].get(j), order[i].get(j).indexOf(currentId));
							List<List<Integer>> partitionAdded = addPartition(order[i], j, partitioned);
							order[i] = partitionAdded;
							for (int l = 0; l < partitioned.get(1).size(); l++) {
								nodes.get(Integer.toString(partitioned.get(1).get(l))).name += "New";
							} 
							j++;
						}
					}
					
				}
			}
		}
		return order;
	}
	
	//Nodes HashMap and order Array as input. If an aggregated node changes
	//its name, the aggregated node is split into two at the name swap
	//intersection. Returns a new order Array with name swaps configured.
	public static List<List<Integer>>[] configureNameSwaps(HashMap<String, myNode> nodes, List<List<Integer>>[] order) {
		for (int i = 0; i < order.length; i++) {
			for (int j = 0; j < order[i].size(); j++) {
				for (int k = 0; k < order[i].get(j).size()-1; k++) {
					int currentId = order[i].get(j).get(k);
					int nextId = order[i].get(j).get(k+1);
					String currentName = nodes.get(Integer.toString(currentId)).name;
					String nextName = nodes.get(Integer.toString(nextId)).name;
					if ( !(currentName.contentEquals(nextName)) ) {
						List<List<Integer>> partitioned = splitListAtIdx(order[i].get(j), order[i].get(j).indexOf(currentId));
						List<List<Integer>> partitionAdded = addPartition(order[i], j, partitioned);
						order[i] = partitionAdded;
					}
				}
			}
		}
		return order;
	}
	
	//A List<Integer> to be split and an index for splitting as input.
	//Returns the list partitioned into two at the splitting index.
	public static List<List<Integer>> splitListAtIdx(List<Integer> list, int index) {
		List<List<Integer>> splitList = new ArrayList<>();
		List<Integer> firstHalf = new ArrayList<>();
		List<Integer> secondHalf = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			if(i <= index) firstHalf.add(list.get(i));
			else if (i > index) secondHalf.add(list.get(i));
		}
		splitList.add(firstHalf);
		splitList.add(secondHalf);
		return splitList;
	}
	
	//List of lists, aggregated node index in the list of lists, and the aggregated
	//node partitioned into two as input. Adds the partitioned list into the list of
	//lists at the aggregated node index. Returns the list of lists with the partitioned
	//aggregated node added.
	public static List<List<Integer>> addPartition(List<List<Integer>> list, int nodeInList, List<List<Integer>> partition) {
		List<List<Integer>> backList = list.subList(0, nodeInList);
		List<List<Integer>> frontList = list.subList(nodeInList+1, list.size());
		List<List<Integer>> partitionAdded = new ArrayList<>(backList);
		for (int i = 0; i < partition.size(); i++) {
			partitionAdded.add(partition.get(i));
		}
		partitionAdded.addAll(frontList);
		
		return partitionAdded;
	}
	
	//Node HashMap as input. Returns the node HashMap with sizes set to
	//the sum of each nodes' children data. 
	public static HashMap<String, myNode> recalculateParentData(HashMap<String, myNode> nodes) {
		for (String key : nodes.keySet()) {
			Double newData = getChildrenData(nodes, key);
			nodes.get(key).size = newData;
		}
		return nodes;
	}
	
	//Nodes HashMap and key as input. Recursively iterates through the tree hierarchy
	//and adds the data sizes of children, grandchildren etc. to the node
	//corresponding to the input key. Returns the nodes HashMap with the data size changed.
	public static Double getChildrenData(HashMap<String, myNode> nodes, String key) {
		Double returnData = null;
		List<Integer> children = nodes.get(key).children;
		if(children.size() > 0) {
			Double childrenData = 0.0d;
			for (int i = 0; i < children.size(); i++) {
				myNode child = nodes.get(Integer.toString(children.get(i)));
				if (child.children.size() > 0) childrenData += getChildrenData(nodes, Integer.toString(child.id));
				else {
					childrenData += nodes.get(Integer.toString(child.id)).size;	
				}
				returnData = childrenData;
			}
		}
		else {
			returnData = nodes.get(key).size;
		}
		return returnData;
	}
}
