package masterwebdataproject.clustering;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;

import masterwebdataproject.model.*;

public class DogfoodClustering {
	public static HashSet<Dogfood> dogList;
	public static HashSet<D_Cluster> clusterList;
	public static HashSet<String> clusterCheck;
	public static HashSet<D_Cluster> valuesToAdd;
	public static HashSet<D_Cluster> valuesToRemove;

	public static void clearSets() {
		dogList = new HashSet<Dogfood>();
		clusterList = new HashSet<D_Cluster>();
		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<D_Cluster>();
		valuesToRemove = new HashSet<D_Cluster>();
	}

	// CSV file with all dogfood offers is read and parsed into a HashSet<Dogfood>
	public static void readDataLineByLine(String file) {
		BufferedReader br = null;
		String line = "";

		// prevents from reading headers
		int count = 0;

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			while ((line = br.readLine()) != null) {
				if (count > 0) {
					String[] array = line.split(",");
					if (array[0].length() > 10) {
						dogList.add(new Dogfood(array[2], array[1], array[0]));
					}
				}
				count++;
			}

			System.out.println("[INFO] File has been processed.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// prints given HashSet
	public static void printHashSet(HashSet<?> h) {
		Iterator<?> it = h.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	// Intatiate first round of clusters by looking at each dogfood in the list and
	// create a cluster for it
	public static void createCluster(HashSet<Dogfood> dogList) {
		int count = 0;
		Iterator<Dogfood> it = dogList.iterator();
		while (it.hasNext()) {
			D_Cluster c = new D_Cluster(it.next());
			if ((c.getIDGenerator() == 0) & (count == 0)) {
				c.restartInteger();
				count++;
			}

			Iterator<String> i = c.getIDValues().iterator();

			System.out.println("ClusterID: " + c.getClusterId() + " created [identifiers: " + i.next() + "]");
			clusterList.add(c);
		}
	}

	// merge both clusters into one
	public static D_Cluster merge(D_Cluster c1, D_Cluster c2) {
		D_Cluster c = new D_Cluster();

		Iterator<Dogfood> it1 = c1.getList().iterator();
		while (it1.hasNext()) {
			c.addItem(it1.next());
			c.updateIDValues();
		}
		Iterator<Dogfood> it2 = c2.getList().iterator();
		while (it2.hasNext()) {
			c.addItem(it2.next());
			c.updateIDValues();
		}

		System.out.println("New cluster " + c.getClusterId() + ", from " + c1.getClusterId() + c2.getClusterId() + " "
				+ c.getIDValues().toString());

		return c;
	}

	public static boolean hasBeenTested(D_Cluster c1, D_Cluster c2) {
		String s1 = c1.getClusterId() + "" + c2.getClusterId();
		String s2 = c2.getClusterId() + "" + c1.getClusterId();
		boolean found = false;

		// only continue if not identical
		if (c1.getClusterId() != c2.getClusterId()) {

			// check if c1 and c2 has been checked yet
			if (clusterCheck.contains(s1)) {
				found = true;
			} else if (clusterCheck.contains(s2)) {
				found = true;
			} else {
				found = false;
			}
		} else {
			found = true;
		}
		return found;
	}

	public static boolean findOverlap(D_Cluster c1, D_Cluster c2) {

		clusterCheck.add(c1.getClusterId() + "" + c2.getClusterId());
		clusterCheck.add(c2.getClusterId() + "" + c1.getClusterId());

		boolean overlap = false;

		// get the corresponding idValues for both clusters
		HashSet<String> h1 = c1.getIDValues();
		HashSet<String> h2 = c2.getIDValues();

		for (String s : h1) {

			s = s.substring(s.length() - 8, s.length());

		}
		for (String s : h2) {
			s = s.substring(s.length() - 8, s.length());
		}

		for (String id : h1) {
			if (h2.contains(id)) {
				overlap = true;
			}
		}
		return overlap;
	}

	public void testBlocking() {

		/**
		 * sort Identifiers into buckets only compare within the same bucket
		 */
	}

	// Compares clustersfrom clusterList, only if their are distinct and have not
	// been compared in the same round before
	public static void findClusters() {

		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<D_Cluster>();
		valuesToRemove = new HashSet<D_Cluster>();

		Iterator<D_Cluster> it1 = clusterList.iterator();
		int count = 0;
		while (it1.hasNext()) {
			D_Cluster c1 = it1.next();
			Iterator<D_Cluster> it2 = clusterList.iterator();

			if (!c1.getHasBeenMatched()) {
				while (it2.hasNext()) {
					D_Cluster c2 = it2.next();

					if ((!hasBeenTested(c1, c2)) && (!c2.getHasBeenMatched()) && (!c1.getHasBeenMatched())) {
						boolean test = findOverlap(c1, c2);
						count++;
						System.out.println(count + ": " + c1.getClusterId()+c2.getClusterId());
						if (test) {
							D_Cluster new_cluster = merge(c1, c2);
							valuesToAdd.add(new_cluster);
							valuesToRemove.add(c1);
							valuesToRemove.add(c2);
							c1.setHasBeenMatched(true);
							c2.setHasBeenMatched(true);
						}
					}
				}
			}
		}
		clusterList.removeAll(valuesToRemove);
		clusterList.addAll(valuesToAdd);
	}

	public static void clustering() {
		int size_before = clusterList.size();
		int size_after = 0;
		int count = 1;

//		while (size_before != size_after) {

			System.out.println("\n***** Find Clusters Round " + count);

			size_before = clusterList.size();

			findClusters();

			size_after = clusterList.size();
			System.out.println("Clustersize now: " + size_before + " / " + size_after);
			count++;

//		}
	}

	public static void main(String[] args) {
		// resets all HashSets
		clearSets();

		// reads file into Dogfood HashSet
		System.out.println("\n***** New File Read *********\n");
		readDataLineByLine("\\Users\\ankmi\\Desktop\\Dogfood.csv");

		// turns dogfood items into single clusters
		System.out.println("\n***** Initiate Clusters *********\n");
		createCluster(dogList);

		// iterates over cluster set until stop criteria is met (number of clusters does
		// not change)
		System.out.println("\n***** Start Clustering *********\n");
		clustering();

//		Writer writer = null;
//
//		try {
//		    writer = new BufferedWriter(new OutputStreamWriter(
//		          new FileOutputStream("filename.txt"), "utf-8"));
//		    for (Cluster c: clusterList) {
//		    	writer.write(c.toString() + "\n");
//		    }
//		} catch (IOException ex) {
//		    // Report
//		} finally {
//		   try {writer.close();} catch (Exception ex) {/*ignore*/}
//		}

		String filepath = "clusters_dogfood.csv";
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(filepath));
			StringBuilder sb = new StringBuilder();
			sb.append("clusterID");
			sb.append(',');
			sb.append("Values");
			sb.append(',');
			sb.append("Size");
			sb.append(',');
			sb.append("DogIDs");
			sb.append('\n');
			for (D_Cluster c : clusterList) {
				sb.append(c.toString());
				sb.append("\n");
			}

			pw.write(sb.toString());
			pw.close();
			System.out.println("done!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
