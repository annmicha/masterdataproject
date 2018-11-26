package masterdataproject.model;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class D_Cluster {

	private static final AtomicInteger idGenerator = new AtomicInteger(1);
	protected String cluster_id;
	protected HashSet<Dogfood> dogList;
	protected HashSet<String> idValues;
	protected int size;
	protected boolean hasBeenMatched;
	
	//creates plain new, empty cluster
	public D_Cluster() {
		cluster_id = "cluster_" + idGenerator.getAndIncrement();
		dogList = new HashSet<Dogfood>();
		idValues = new HashSet<String>();
		size = dogList.size();
		hasBeenMatched = false;
		
		updateIDValues();	
	}
	

	// creates new cluster based on a hashset of dogfood offers
	public D_Cluster(HashSet<Dogfood> hs) {
		cluster_id = "cluster_" + idGenerator.getAndIncrement();
		dogList = hs;
		idValues = new HashSet<String>();	
		size = dogList.size();
		hasBeenMatched = false;
		
		updateIDValues();	
	}
	
	// creates a new Cluster based on one single 
	public D_Cluster(Dogfood d) {
		cluster_id = "cluster_" + idGenerator.getAndIncrement();
		dogList = new HashSet<Dogfood>();
		dogList.add(d);
		idValues = new HashSet<String>();
		size = dogList.size();
		hasBeenMatched = false;
		
		updateIDValues();
	}
	
	// clears IDValues list and adds all identifiers from dogList
	public void updateIDValues() {
		idValues.clear();
		
		Iterator<Dogfood> it = dogList.iterator();
		while(it.hasNext()) {
			idValues.add(it.next().getUPC());
		}
	}
	
	public void addItem(Dogfood d) {
		dogList.add(d);
		size = dogList.size();
	}
	
	public void removeItem(Dogfood d) {
		dogList.remove(d);
		size = dogList.size();
	}
	
	public HashSet<Dogfood> getList(){
		return dogList;
	}
	
	// returns ID Values
	public HashSet<String> getIDValues(){
		return idValues;
	}
	
	public String getClusterId() {
		return cluster_id;
	}
	
	public int getSize() {
		return size;
	}
	
	public void restartInteger() {
		idGenerator.set(0);
	}
	
	public int getIDGenerator() {
		return idGenerator.get();
	}
	
	public boolean getHasBeenMatched() {
		return hasBeenMatched;
	}
	
	public void setHasBeenMatched (boolean b) {
		this.hasBeenMatched = b;
	}
	
	public String toString() {
		String s = "";
		for (Dogfood d : dogList) {
			s += d.getID();
		}
		return this.cluster_id + ", " + 
				this.idValues.toString() + ", " + 
				this.size + ", " +  s;
	}
}
