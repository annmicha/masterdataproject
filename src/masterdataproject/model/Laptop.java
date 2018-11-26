package masterdataproject.model;

import java.util.HashSet;

public class Laptop {

	protected String id;
	private String name;
	private HashSet<String> idValues; 
//	private String upc;

	public Laptop (String identifier, String name, HashSet<String> set){
//	public Laptop (String identifier, String name, String upc) {
		this.id = identifier;
		this.setName(name.replace("\"", ""));
		idValues = set;
//		idValues.add(upc)
//		this.upc = upc;
	}

	public String toString() {
		String values = "";
		for (String s: idValues) {
			values = values + ", " + s;
		}
		
		String s = "ID: " + id + ", name: " + name + ", id_values: " + values;
//		String s = "ID: " + id + ", name: " + name + ", id_values: " + upc;
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
	
	
//	public String getIDValue() {
//		return upc;
//	}
//	
//	public void setIDValue(String s) {
//		upc = s;
//	}
	
}
