package masterwebdataproject.model;

import java.util.HashSet;

public class DogfoodMulti {

	protected String id;
	private String name;
	private HashSet<String> idValues;

	public DogfoodMulti (String identifier, String name, HashSet<String> set){
		this.id = identifier;
		this.setName(name.replace("\"", ""));
		idValues = set;
	}

	public String toString() {
		String values = "";
		for (String s: idValues) {
			values = values + ", " + s;
		}
		
		String s = "ID: " + id + ", name: " + name + ", id_values: " + values;
		return s;
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<String> getIDValues() {
		return this.idValues;
	}

	public void setIDValues(HashSet<String> identifiers) {
		this.idValues = identifiers;
	}	
	
}
