package algorithmHT2020;

import java.util.ArrayList;
import java.util.List;

public class myNode {
	int id;
	int position;
	String name;
	int time;
	double size;
	int layer;
	int parent = 0;
	List<Integer> becomes = new ArrayList<>();
	List<Integer> was = new ArrayList<>();
	List<Integer> children = new ArrayList<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
   public String getValue() { 
        String output = this.id + ": "+ " " + this.name + ", " + this.position + " " + this.time + " " + this.size + " " + this.layer;
        return output; 
    }

}

