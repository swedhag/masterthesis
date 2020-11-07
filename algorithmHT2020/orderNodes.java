package algorithmHT2020;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Collections;

import java.io.IOException;

public class orderNodes {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = readNTG.generateNodes("visciousFingers.ntg");

		int initNodeCnt = 0;
		for (String key : nodes.keySet()) {
			if ( nodes.get(key).time == 40 ) initNodeCnt++;
		}

		List<List<Integer>>[] order = getOrders(nodes);
	}
	
	//HashMap nodes as input. Returns the deepest tree level integer found in nodes.
	public static int maxLayer (HashMap<String, myNode> nodes) {
		int maxLayer = 0;
		for (String key : nodes.keySet()) {
			if (nodes.get(key).layer > maxLayer) maxLayer = nodes.get(key).layer;
		}
		return maxLayer;
	}
	
	//Node HashMap as input. Finds the maximum number of nodes found at any
	//timestep of the time-variant tree. Returns an int[] with elements
	//corresponding to each tree level.
	public static int[] maxPositionsByLayer (HashMap<String, myNode> nodes) {
		int maxLayer = maxLayer(nodes);
		int[] maxPositionsByLayer  = new int[maxLayer + 1];
		for (int i = 0; i <= maxLayer; i++) {
			int maxPosition = 0;
			for (String key : nodes.keySet()) {
				if (nodes.get(key).layer == i) {
					if (nodes.get(key).position > maxPosition) maxPosition = nodes.get(key).position;	
				}
			}
			maxPositionsByLayer[i] = maxPosition;	
		}
		return maxPositionsByLayer;
	}
	
	//Node HashMap and tree level as input. Returns a List<List<Integer>> array with aggregated
	//nodes added to the correct indices of the Array, where nodes are of the specified level.
	public static List<List<Integer>>[] getLayerOrder(HashMap<String, myNode> nodes, int layer) {
		Integer[] minMaxTime = aggregatedNodes.computeTimespan(nodes);
		int initialPositions = 0; 
		for (String key: nodes.keySet()) {
			if ( (nodes.get(key).time == minMaxTime[0]) && (nodes.get(key).layer == layer) ) initialPositions++;
		}
		
		List<Integer>[] evolvingPositions = new List[initialPositions];
		for (int i = 0; i < evolvingPositions.length; i++) evolvingPositions[i] = new ArrayList<>();
		for (String key: nodes.keySet()) { 
			if ( (nodes.get(key).time == minMaxTime[0]) && (nodes.get(key).layer == layer)) {
				evolvingPositions[nodes.get(key).position].add(nodes.get(key).id);
			}
		}
		
		TreeMap<Integer, List<List<Integer>>> ghostPositions = new TreeMap<>();
		int currentTime = minMaxTime[0];
		while (currentTime + 1 <= minMaxTime[1] ) {
			System.out.println("XXXXXXXXXXXXXXXXXX");
			System.out.println(Arrays.deepToString(evolvingPositions));
			for (int i = 0; i < evolvingPositions.length; i++) {
				List<Integer> activeEvolvingNode = evolvingPositions[i];
				String activeNode = Integer.toString(activeEvolvingNode.get(activeEvolvingNode.size()-1));
				List<Integer> becomes = nodes.get(activeNode).becomes;
				System.out.println(activeNode + "->" + becomes);
				if (becomes.size() == 0) {
					ghostPositions = addGhostPosition(nodes, ghostPositions, evolvingPositions[i], nodes.get(activeNode).position);
					ghostPositions = updateGhostPositions(ghostPositions, becomes, nodes.get(activeNode).position, "done");
					evolvingPositions = removeEvolvingNodeAtIndex(evolvingPositions, i);
					i--;
				}
				if (becomes.size() == 1) {
					//Regular
					if (nodes.get(Integer.toString(becomes.get(0))).position == i) {
						evolvingPositions[i].add(becomes.get(0));
					}
					//Merge
					else if (nodes.get(Integer.toString(becomes.get(0))).position < i) {
							ghostPositions = updateGhostPositions(ghostPositions, becomes, i, "merge");
							ghostPositions = addGhostPosition(nodes, ghostPositions, evolvingPositions[i], i);
							evolvingPositions = removeEvolvingNodeAtIndex(evolvingPositions, i);
							i--;
					}
					//New node
					else {
						//System.out.println(nodes.get(activeNode).getValue() + "->" +  nodes.get(Integer.toString(becomes.get(0))).getValue());
						myNode newNode = findNewNode(nodes, currentTime+1, i);
						//System.out.println(newNode.getValue());
						evolvingPositions = addNewNode(newNode, evolvingPositions, i);
						List<Integer> fakeBecomes = new ArrayList<>();
						fakeBecomes.add(newNode.id);
						ghostPositions = updateGhostPositions(ghostPositions, fakeBecomes, i, "new");
					}
				}
				//Split
				if (becomes.size() > 1) {
					becomes = sortAccordingToPositions(nodes, becomes);
					//And merge
					if( i > 0 && becomes.get(0).equals(evolvingPositions[i-1].get(evolvingPositions[i-1].size()-1)) ) {
						becomes.remove(0);
					}
					if ( (i < evolvingPositions.length - 1) && (nodes.get(Integer.toString(evolvingPositions[i+1].get(evolvingPositions[i+1].size()-1))).becomes.get(0).equals(becomes.get(becomes.size()-1)))) {
						becomes.remove(becomes.size()-1);
					}
					ghostPositions = updateGhostPositions(ghostPositions, becomes, i, "split");
					ghostPositions = addGhostPosition(nodes, ghostPositions, evolvingPositions[i], i);
					evolvingPositions = removeEvolvingNodeAtIndex(evolvingPositions, i);
					evolvingPositions = addSplitNodes(nodes, evolvingPositions, becomes);
					i += becomes.size()-1;
				}
			}
			//System.out.println(Arrays.deepToString(evolvingPositions));
			int nodeCnt = 0;
			List<Integer> nodesAtTimestep = new ArrayList<>();
			for (String key : nodes.keySet()) {
				if (nodes.get(key).layer == layer && nodes.get(key).time == currentTime+1 ) {
					nodeCnt++;
					nodesAtTimestep.add(nodes.get(key).id);
				}
			}

			if (evolvingPositions.length != nodeCnt) {
				for (int i = 0; i < evolvingPositions.length; i++) {
						if (nodesAtTimestep.contains(evolvingPositions[i].get(evolvingPositions[i].size()-1))) {
							nodesAtTimestep.remove(nodesAtTimestep.indexOf(evolvingPositions[i].get(evolvingPositions[i].size()-1)));
						}
				}
				for (int i = 0; i < nodesAtTimestep.size(); i++) {
					List<Integer> newNode = new ArrayList<>();
					myNode addedLast = nodes.get(Integer.toString(nodesAtTimestep.get(i)));
					evolvingPositions = addNewNode(addedLast, evolvingPositions, evolvingPositions.length);
				}
			}
			System.out.println(Arrays.deepToString(evolvingPositions));
			for (Integer key : ghostPositions.keySet()) {
				System.out.println(key + " " + ghostPositions.get(key));
			}
			currentTime++;
		}
		List<List<Integer>> finalPositions[] = computeFinalPositions(nodes, ghostPositions, evolvingPositions, layer);
 		return finalPositions;
	}
	
	//Nodes HashMap, ghostNodes TreeMap, evolvingNodes Array and tree level Integer as input.
	//Creates a final order List<List<Integer>[] object and adds ghostNodes and evolvingNodes
	//to it's indices. Returns the List<List<Integer>>[] final order object.
	public static List<List<Integer>>[] computeFinalPositions(HashMap<String, myNode> nodes, TreeMap<Integer, List<List<Integer>>> ghostPositions, List<Integer>[] evolvingPositions, int layer) {
		int[] totalPositions = maxPositionsByLayer(nodes);
		List<List<Integer>>[] finalPositions = new List[totalPositions[layer]+1];
		Arrays.setAll(finalPositions, element -> new ArrayList<>());
		for (Integer key : ghostPositions.keySet()) {
			finalPositions[key] = ghostPositions.get(key);
		}
		for (int i = 0; i < evolvingPositions.length; i++) {
			if (finalPositions[i].isEmpty()) {
				finalPositions[i].add(evolvingPositions[i]);	
			}
			else {
				int idx = 0;
				int evolvingNodeFirstPosition = nodes.get(Integer.toString(evolvingPositions[i].get(0))).position;
				for (int j = 0; j < finalPositions[i].size(); j++) {
					int ghostNodeFirstPosition = nodes.get(Integer.toString(finalPositions[i].get(j).get(0))).position;
					if (ghostNodeFirstPosition <= evolvingNodeFirstPosition ) {
						idx++;
					}
				}
				finalPositions[i].add(idx, evolvingPositions[i]);
			}
		}
	return finalPositions;
	}
	
	//Nodes HashMap, tree level Integer and timestamp Integer as input.
	//Returns the number of nodes in the tree, at the specified tree level
	//and the specified timestamp.
	public static int findSizeAtTimeStep(HashMap<String, myNode> nodes, int layer,int time) {
		int size = 0;
		for (String key : nodes.keySet() ) {
			if ( (nodes.get(key).time == time) && (nodes.get(key).layer == layer)) size++;
		}
		return size;
	}
	
	//Nodes HashMap, timestamp Integer and positional Integer as input.
	//Returns the myNode class instance found at the corresponding timestamp and position
	//The function is called when a new node has been added the tree.
	//New nodes have no previous nodes in its "was" list
	public static myNode findNewNode(HashMap<String, myNode> nodes, int time, int position) {
		myNode newNode = new myNode();
		for (String key : nodes.keySet()) {
			if ( (nodes.get(key).time == time) && (nodes.get(key).was.isEmpty()) && (nodes.get(key).position == position) ) {
				newNode =  nodes.get(key);
			} 
		}
		return newNode;
	}
	
	//myNode class instance, List<Integer>[] evolvingPositions and positional Integer argument
	//as input. Adds the myNode node's ID in a list to the array of evolving positions, at the index
	//of the specified positional argument. Returns List<Integer>[] evolvingPositions with the new node added.
	public static List<Integer>[] addNewNode(myNode addedNode, List<Integer>[] evolvingPositions, int position) {
		if (position != evolvingPositions.length ) {
			if (position != 0) {
				List<Integer>[] backArray = Arrays.copyOfRange(evolvingPositions, 0,  addedNode.position);
				List<Integer>[] frontArray = Arrays.copyOfRange(evolvingPositions, addedNode.position,  evolvingPositions.length);
				List<Integer>[] newNode = new List[1];
				newNode[0] = new ArrayList<>();
				newNode[0].add(addedNode.id);
				evolvingPositions = new List[backArray.length + newNode.length + frontArray.length];
				System.arraycopy(backArray, 0, evolvingPositions, 0, backArray.length);
				System.arraycopy(newNode, 0, evolvingPositions, backArray.length, newNode.length);
				System.arraycopy(frontArray, 0, evolvingPositions, backArray.length + newNode.length, frontArray.length);	
			}
			else if (position == 0) {
				List<Integer>[] frontArray = Arrays.copyOfRange(evolvingPositions, 0,  evolvingPositions.length);
				List<Integer>[] newNode = new List[1];
				newNode[0] = new ArrayList<>();
				newNode[0].add(addedNode.id);
				evolvingPositions = new List[frontArray.length + 1];
				System.arraycopy(newNode, 0, evolvingPositions, 0, newNode.length);
				System.arraycopy(frontArray, 0, evolvingPositions, newNode.length, frontArray.length);
			}
		}
		else if (position == evolvingPositions.length) {
			List<Integer>[] backArray = Arrays.copyOfRange(evolvingPositions, 0,  addedNode.position);
			List<Integer>[] newNode = new List[1];
			newNode[0] = new ArrayList<>();
			newNode[0].add(addedNode.id);
			evolvingPositions = new List[backArray.length + 1];
			System.arraycopy(backArray, 0, evolvingPositions, 0, backArray.length);
			System.arraycopy(newNode, 0, evolvingPositions, backArray.length, newNode.length);
		}
		return evolvingPositions;
	}
	
	//ghostPositions TreeMap, "becomes" List<Integer>, positional Integer argument and classifier String as input.
	//Depending on the type temporal evolvements occuring in the evolvingPositions array, the function updates
	//ghost nodes positional values accordingly. Returns an updated ghostPositions TreeMap.
	public static TreeMap<Integer, List<List<Integer>>> updateGhostPositions(TreeMap<Integer, List<List<Integer>>> ghostPositions, List<Integer> becomes, int position, String type) {
		TreeMap<Integer, List<List<Integer>>> treeMapCopy = copyGhostPositions(ghostPositions);
		//Merge
		if ( (becomes.size() == 1) && (type.contentEquals("merge")) ) {
			for (Integer key : ghostPositions.keySet()) {
				if ((key > position) ) {
					List<List<Integer>> positionUpdate = ghostPositions.get(key);
					int newKey = key-1;
					treeMapCopy.remove(key);
					treeMapCopy.put(newKey, positionUpdate);
				}
			}
		}
		//New
		else if ( (becomes.size() == 1) && (type.contentEquals("new")) ) {
			for (Integer key : ghostPositions.keySet()) {
				if ((key >= position) ) {
					List<List<Integer>> positionUpdate = ghostPositions.get(key);
					int newKey = key+1;
					treeMapCopy.remove(key);
					treeMapCopy.put(newKey, positionUpdate);
				}
			}
		}
		//Split
		else if ( (becomes.size() > 1)  && (type.contentEquals("split")) ) {
			for (Integer key : ghostPositions.keySet()) {
				if (key >= (position + becomes.size()-1) ) {
					List<List<Integer>> positionUpdate = ghostPositions.get(key);
					int newKey = key + (becomes.size()-1);
					treeMapCopy.remove(key);
					treeMapCopy.put(newKey, positionUpdate);
				}
			}
		}
		//Done
		else if ((becomes.size() == 0) && (type.contentEquals("done"))) {
			for (Integer key : ghostPositions.keySet()) {
				if ( key > position ) {
					List<List<Integer>> positionUpdate = ghostPositions.get(key);
					int newKey = key - 1;
					treeMapCopy.remove(key);
					treeMapCopy.put(newKey, positionUpdate);
				}
			}
		}
		return treeMapCopy;
	}
	
	//Nodes HashMap, ghostPositions TreeMap, aggregated node List<Integer> and positional Integer argument as input.
	//Adds an aggregated node to the ghostPositions TreeMap at the index specified by the positional argument.
	//Returns the new ghostPositions TreeMap.
	public static TreeMap<Integer, List<List<Integer>>> addGhostPosition(HashMap<String, myNode> nodes, TreeMap<Integer, List<List<Integer>>> ghostPositions, List<Integer> evolvingNode, int position){
		TreeMap<Integer, List<List<Integer>>> treeMapCopy = copyGhostPositions(ghostPositions);
		if (ghostPositions.containsKey(position)) {
			List<List<Integer>> listUpdate = ghostPositions.get(position);
			int idx = 0;
			int newGhostFirstPosition = nodes.get(Integer.toString(evolvingNode.get(0))).position;
			for (int i = 0; i < listUpdate.size(); i++) {
				int oldGhostFirstPosition = nodes.get(Integer.toString(listUpdate.get(i).get(0))).position;
				if (oldGhostFirstPosition < newGhostFirstPosition) idx++;
			}
			listUpdate.add(idx, evolvingNode);
			treeMapCopy.remove(position);
			treeMapCopy.put(position, listUpdate);
		}
		else {
			List<List<Integer>> ghostNodes = new ArrayList<>();
			ghostNodes.add(evolvingNode);
			treeMapCopy.put(position, ghostNodes);
		}
		return treeMapCopy;
	}
	
	//Nodes HashMap, evolvingPositions List<Integer>[] and List<Integer> newNodes as input.
	//Adds all newNodes resulting from a node split to the evolvingPositions array, at the index
	//specified by the positional argument. Returns updated evolvingPositions array.
	public static List<Integer>[] addSplitNodes(HashMap<String, myNode> nodes, List<Integer>[] evolvingPositions, List<Integer> newNodes) {
		for (int j = 0; j < newNodes.size(); j++) {
			myNode freshNode = nodes.get(Integer.toString(newNodes.get(j)));
			List<Integer>[] backArray = Arrays.copyOfRange(evolvingPositions, 0,  freshNode.position);
			List<Integer>[] frontArray = Arrays.copyOfRange(evolvingPositions, freshNode.position,  evolvingPositions.length);
			List<Integer>[] newNode = new List[1];
			newNode[0] = new ArrayList<>();
			newNode[0].add(freshNode.id);
			evolvingPositions = new List[backArray.length + newNode.length + frontArray.length];
			System.arraycopy(backArray, 0, evolvingPositions, 0, backArray.length);
			System.arraycopy(newNode, 0, evolvingPositions, backArray.length, newNode.length);
			System.arraycopy(frontArray, 0, evolvingPositions, backArray.length + newNode.length, frontArray.length);
		}
		return evolvingPositions;
	}
	
	//ghostPositions TreeMap as input. Prints the contents of the ghostPositions TreeMap.
	public static void printGhostPositions(TreeMap<Integer, List<List<Integer>>> ghostPositions) {
		for (Integer key : ghostPositions.keySet()) {
			System.out.println(key + " " + ghostPositions.get(key));
		}
	}
	
	//ghostPositions TreeMap as input. Creates a deep copy of the ghostPositions TreeMap.
	//Returns the deep copy.
	public static TreeMap<Integer, List<List<Integer>>> copyGhostPositions(TreeMap<Integer, List<List<Integer>>> original) {
		TreeMap<Integer, List<List<Integer>>> copy = new TreeMap<>();
		for (Integer key : original.keySet()) {
			copy.put(key, original.get(key));
		}
		return copy;
	}
	
	//evolvingPositions List<Integer>[] and positional Integer argument as input.
	//Removes the aggregated node located at the index of evolvingPositions specifed 
	//by the positional argument. Returns updated evolvingNodes List<Integer>[]
	public static List<Integer>[] removeEvolvingNodeAtIndex(List<Integer>[] evolvingPositions, int index) {
		List<Integer>[] backArray = Arrays.copyOfRange(evolvingPositions, 0,  index);
		List<Integer>[] frontArray = Arrays.copyOfRange(evolvingPositions, index+1,  evolvingPositions.length);
		evolvingPositions = new List[backArray.length + frontArray.length];
		System.arraycopy(backArray, 0, evolvingPositions, 0, backArray.length);
		System.arraycopy(frontArray, 0, evolvingPositions, backArray.length, frontArray.length);
		return evolvingPositions;
	}
	
	//Nodes HashMap and "becomes" List<Integer> as input. Orders the nodes in "becomes" List
	//based on their y-values(lowest in the front, highest in the back)
	//Returns the sorted "becomes" list.
	public static List<Integer> sortAccordingToPositions(HashMap<String, myNode> nodes, List<Integer> becomes) {
		List<Integer> positions = new ArrayList<>();
		for (int i = 0; i < becomes.size(); i++) {
			positions.add(nodes.get(Integer.toString(becomes.get(i))).position);
		}
		List<Integer> sortedBecomes = new ArrayList<>();
		for (int i = Collections.min(positions); i <= Collections.max(positions); i++) {
			for (int j = 0; j < positions.size(); j++) {
				if (positions.get(j) == i) sortedBecomes.add(becomes.get(j));	
			}
		}
		return sortedBecomes;
	}
	
	//Nodes HashMap as input. Assembles the orders of all levels of the temporal tree.
	//Returns List<List<Integer>[] containing all orders, at all levels.
	public static List<List<Integer>>[] getOrders(HashMap<String, myNode> nodes) {
		int[] maxPositionsByLayer = orderNodes.maxPositionsByLayer(nodes);
		List<List<Integer>>[] finalPositions = new List[Arrays.stream(maxPositionsByLayer).sum() + maxPositionsByLayer.length];
		int idx = 0;
		for (int i = 0; i < maxPositionsByLayer.length; i++) {
			List<List<Integer>>[] layerPositions = orderNodes.getLayerOrder(nodes, i);
			System.out.println(Arrays.deepToString(layerPositions));
			for (int j = 0; j < layerPositions.length; j++) {
				finalPositions[idx] = layerPositions[j];
				idx++;
			}
		}
		return finalPositions;
	}
	
}
