package masterwebdataproject.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class DMulti_Cluster {

	private static final AtomicInteger idGenerator = new AtomicInteger(1);
	protected String cluster_id;
	protected String i;
	protected HashSet<DogfoodMulti> itemList;
	protected HashSet<String> idValues;
	protected String idValue;
	protected int size;
	protected boolean hasBeenMatched;

	// creates plain new, empty cluster
	public DMulti_Cluster() {
		i = "" + idGenerator.getAndIncrement();
		cluster_id = "cluster_" + i;
		itemList = new HashSet<DogfoodMulti>();
		idValues = new HashSet<String>();
		size = itemList.size();
		hasBeenMatched = false;

		updateIDValues();
	}

	// creates new cluster based on a hashset of dogfood offers
	public DMulti_Cluster(HashSet<DogfoodMulti> hs) {
		i = "" + idGenerator.getAndIncrement();
		cluster_id = "cluster_" + i;
		itemList = hs;
		idValues = new HashSet<String>();
		size = itemList.size();
		hasBeenMatched = false;

		updateIDValues();
	}

	// creates a new Cluster based on one single
	public DMulti_Cluster(DogfoodMulti d) {
		i = "" + idGenerator.getAndIncrement();
		cluster_id = "cluster_" + i;
		itemList = new HashSet<DogfoodMulti>();
		itemList.add(d);
		idValues = new HashSet<String>();
		size = itemList.size();
		hasBeenMatched = false;

		updateIDValues();
	}

	// clears IDValues list and adds all identifiers from itemList
	public void updateIDValues() {
		idValues.clear();

		Iterator<DogfoodMulti> it = itemList.iterator();
		while (it.hasNext()) {
			DogfoodMulti l = it.next();
//			idValues.add(l.getIDValue());

			Iterator<String> it2 = l.getIDValues().iterator();

			while (it2.hasNext()) {
				String s = it2.next();
				if (s != "") {
					idValues.add(s);
				}
			}
		}
	}

	public void addItem(DogfoodMulti d) {
		itemList.add(d);
		size = itemList.size();
	}

	public void removeItem(DogfoodMulti d) {
		itemList.remove(d);
		size = itemList.size();
	}

	public HashSet<DogfoodMulti> getList() {
		return itemList;
	}

	// returns ID Values
	public HashSet<String> getIDValues() {
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

	public String getID() {
		return i;
	}

	public boolean getHasBeenMatched() {
		return hasBeenMatched;
	}

	public void setHasBeenMatched(boolean b) {
		this.hasBeenMatched = b;
	}

	public String toString() {
		String s = "";
		String i = "";
		for (DogfoodMulti l : itemList) {
			s += l.getID() + "; ";
		}
		for (String v : idValues) {
			i += v + "; ";
		}
		return this.cluster_id + ", " + i + ", " + this.size + "," + s;
	}

	public String toOfferString() {
		String s = "";
		String i = "";
		for (DogfoodMulti l : itemList) {
			s += l.getID() + "; ";
		}
		for (String v : idValues) {
			i += v + "; ";
		}
		return this.cluster_id + ", " + i + ", " + this.size + "," + s;
	}
}
