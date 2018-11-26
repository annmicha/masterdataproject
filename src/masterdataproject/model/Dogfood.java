package masterdataproject.model;


public class Dogfood {

	protected String id;
	private String name;
	// private HashSet<String> idValues; in case of multiple identifiers
	private String upc;

	public Dogfood (String identifier, String name, String upc){
		this.id = identifier;
		this.setName(name);
		//idValues = new HashSet<String>();
		//idValues.add(upc)
		this.upc = upc;
	}

	public String toString() {
		String s = "ID: " + id + ", name: " + name + ", upc: " + upc;
		System.out.println(s);
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

	public String getUPC() {
		return upc;
	}

	public void setIdentifiers(String identifiers) {
		this.upc = identifiers;
	}	
	
	
}
