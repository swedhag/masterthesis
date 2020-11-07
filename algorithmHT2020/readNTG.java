package algorithmHT2020;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class readNTG {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, myNode> nodes = generateNodes("visciousFingers.ntg");
	}
	
	//NTG file path as input, returns HashMap of the nodes (N -> myNode) in the file,
	//with temporal edges (ET) and hierarchical edges (EN) added as attributes
	public static HashMap<String, myNode> generateNodes (String path) throws IOException {
		String sDetail = readFromFile(path);
		HashMap<String, myNode> nodes = findNodes(sDetail);
		
		HashMap<String, List<Integer>> temporalEdges = findTempEdges(sDetail);
		nodes = addTempEdges(nodes, temporalEdges);
		
		HashMap<String, List<Integer>> hierarchyEdges = findHierarchyEdges(sDetail);
		nodes = addHierarchyEdges(nodes, hierarchyEdges);
		return nodes;
	}
	
	//NTG file path as input, returns the file as String object 
	public static String readFromFile(String fileName) throws IOException {
		String path = new File("").getAbsolutePath() + "\\datasets\\temporaltrees\\" + fileName;
		File file = new File(path);
		Scanner input = new Scanner(file);
		StringBuffer buf = new StringBuffer();
		while (input.hasNextLine()) {
		    buf.append(input.nextLine());
		    buf.append("\n");
		}
		String s = buf.toString();
		s = s.replaceAll("\\s+","");
		return s;
	}
	
	//NTG String as input, locates all nodes in the list "N" and generates myNode class
	//instances for each of them. Returns HashMap of all myNodes generated, with ids as keys
	public static HashMap<String, myNode> findNodes(String sDetail){
		HashMap<String, myNode> nodes = new HashMap<String, myNode>();
		String hardCoded = sDetail.substring(sDetail.indexOf("\"N\"")+5, sDetail.length() - 1);
		int sum = 0;
		while (hardCoded.length()>0) {
			int t_index = hardCoded.indexOf("\"t\"");
			int closing_index = hardCoded.substring(t_index, hardCoded.length()-1).indexOf("}") + 1;
			String node =  hardCoded.substring(0,t_index+closing_index);
			hardCoded = hardCoded.substring(t_index + closing_index+1, hardCoded.length());
			
			String name = node.substring(0,node.indexOf(":")-1);
			String id = name.substring(1, name.indexOf("_"));
			name = name.substring(name.indexOf("_")+1, name.lastIndexOf("_"));
			String layer = node.substring(node.indexOf("\"l\":")+4,node.indexOf(","));
			String size = node.substring(node.indexOf("\"w\"")+4, node.indexOf("\"x\"")-1);
			String time = node.substring(node.indexOf("\"x\"")+4, node.indexOf("\"y\"")-1);
			String position = node.substring(node.indexOf("\"y\"")+4, node.indexOf("\"t\"")-2);
			
			myNode q;
			q = new myNode();
			q.id = Integer.parseInt(id);
			q.name = name;
			q.position = Integer.parseInt(position);
			q.time = Integer.parseInt(time);
			q.size = Double.parseDouble(size);
			q.layer = Integer.parseInt(layer);
			nodes.put(id,q);
		}
		return nodes;
	}
	
	//NTG String as input, locates all temporal edges in the list "ET".
	//Returns HashMap with "was" as key and "becomes" as value
	public static HashMap<String, List<Integer>> findTempEdges(String sDetail){
		HashMap<String, List<Integer>> tempEdges = new HashMap<String, List<Integer>>();
		String hardCoded = sDetail.substring(sDetail.indexOf("\"ET\"")+6, sDetail.indexOf("\"N\"")-2);
		//System.out.println(hardCoded);
		List<String> temporalLayers = new ArrayList<>();
		while (hardCoded.contains("}")) {
			temporalLayers.add(hardCoded.substring(hardCoded.indexOf(":{")+2,hardCoded.indexOf("}")));
			hardCoded = hardCoded.substring(hardCoded.indexOf("}")+1,hardCoded.length());
		}
		for (int i = 0; i < temporalLayers.size();i++) {
			String layer = temporalLayers.get(i).replaceAll("],", "]q");
			String[] edgeArr = layer.split("q");
			for (int j = 0; j < edgeArr.length; j++) {
				String name = edgeArr[j].substring(1, edgeArr[j].indexOf(":")-1);
				String becomes = edgeArr[j].substring(edgeArr[j].indexOf("[")+1, edgeArr[j].indexOf("]"));
				if (becomes.contains(",")) {
					String[] becomesArr = becomes.split(",");
					List<Integer> splits = new ArrayList<>();
					for (int k = 0; k < becomesArr.length; k++) {
						splits.add(Integer.parseInt(becomesArr[k]));
					}
					tempEdges.put(name, splits);
				}
				else {
					List<Integer> list = new ArrayList<>();
					list.add(Integer.parseInt(becomes));
					tempEdges.put(name, list);
				}
			}
		}
		return tempEdges;
	}
	
	//Nodes HashMap and temporal edges HashMap as input. Adds "was" and "becomes" to each node.
	//Returns nodes HashMap with temporal information added.
	public static HashMap<String, myNode> addTempEdges(HashMap<String, myNode> nodes, HashMap<String, List<Integer>> tempEdges){
		nodes.forEach((k, v) -> {
			if (tempEdges.containsKey(k)) {
				myNode node = v;
				List<Integer> becomes = tempEdges.get(k);
				node.becomes = becomes;
				for (int i = 0; i < becomes.size(); i++) {
					nodes.get(Integer.toString(becomes.get(i))).was.add(Integer.parseInt(k));
				}
				nodes.put(k, node);
			}
		});
		return nodes;
	}
	
	//NTG String as input, locates all hierarchical edges in the list "EN".
	//Returns HashMap with parent as key and children as value
	public static HashMap<String, List<Integer>> findHierarchyEdges(String sDetail){
		HashMap<String, List<Integer>> hierarchyEdges = new HashMap<String, List<Integer>>();
		String hardCoded = sDetail.substring(sDetail.indexOf("\"EN\"")+6, sDetail.indexOf("\"ET\"")-2);
		List<String> timeDepHierarchy = new ArrayList<>();
		while (hardCoded.contains("}")) {
			timeDepHierarchy.add(hardCoded.substring(hardCoded.indexOf(":{")+2,hardCoded.indexOf("}")));
			hardCoded = hardCoded.substring(hardCoded.indexOf("}")+1,hardCoded.length());
		}
		for (int i = 0; i < timeDepHierarchy.size();i++) {
			String timestamp = timeDepHierarchy.get(i).replaceAll("],", "]q");
			String[] edgeArr = timestamp.split("q");
			for (int j = 0; j < edgeArr.length; j++) {
				String parent = edgeArr[j].substring(1, edgeArr[j].indexOf(":")-1);
				String children = edgeArr[j].substring(edgeArr[j].indexOf("[")+1, edgeArr[j].indexOf("]"));
				
				if (children.contains(",")) {
					String[] childArr = children.split(",");
					List<Integer> childList = new ArrayList<>();
					for (int k = 0; k < childArr.length; k++) {
						childList.add(Integer.parseInt(childArr[k]));
					}
					hierarchyEdges.put(parent, childList);
				}
				else {
					List<Integer> childList = new ArrayList<>();
					childList.add(Integer.parseInt(children));
					hierarchyEdges.put(parent, childList);
				}
			}
		}
		return hierarchyEdges;
	}
	
	//Nodes HashMap and hierarchical edges HashMap as input. Adds parent and children to each node.
	//Returns nodes HashMap with hierarchical information added.
	public static HashMap<String, myNode> addHierarchyEdges(HashMap<String, myNode> nodes, HashMap<String, List<Integer>> hierarchyEdges){
		hierarchyEdges.forEach((k, v) -> {
			nodes.get(k).children = v;
			for (int i = 0; i < v.size(); i++) {
				myNode node = nodes.get(Integer.toString(v.get(i)));
				node.parent = Integer.parseInt(k);
				nodes.put(Integer.toString(v.get(i)), node);
			}
		});
		return nodes;
	}
}
