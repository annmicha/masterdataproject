package masterwebdataproject.clustering;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;

import masterwebdataproject.model.*;

public class LaptopClustering {
	public static HashSet<Laptop> itemList;
	public static HashSet<L_Cluster> clusterList;
	public static HashSet<String> clusterCheck;
	public static HashSet<L_Cluster> valuesToAdd;
	public static HashSet<L_Cluster> valuesToRemove;

	public static void clearSets() {
		itemList = new HashSet<Laptop>();
		clusterList = new HashSet<L_Cluster>();
		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<L_Cluster>();
		valuesToRemove = new HashSet<L_Cluster>();
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

				if ((count > 0)) {
					String[] array = null;
					array = line.split(",");

					if (array.length == 5) {
						// 0 > offer_id
						// 1 > id1
						// 2 > id2
						// 3 > id3
						// 4 > name
						// new laptop (String identifier, String name, HashSet)

						HashSet<String> temp = new HashSet<String>();

						for (int counter = 1; counter < 4; counter++) {
							if (!array[counter].isEmpty()) {
								temp.add(array[counter].replaceAll("\\s", ""));
							}

						}

						itemList.add(new Laptop(array[0].replaceAll("\\s", ""), array[4].replaceAll("\\s", ""), temp));
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
	public static void createCluster(HashSet<Laptop> itemList) {
		int count = 0;
		Iterator<Laptop> it = itemList.iterator();
		while (it.hasNext()) {
			L_Cluster c = new L_Cluster(it.next());
			if ((c.getIDGenerator() == 0) & (count == 0)) {
				c.restartInteger();
				count++;
			}

//			System.out.println("ClusterID: " + c.getClusterId() + ", " + c.getIDValues().toString());
			clusterList.add(c);
		}
		System.out.println("[INFO] " + itemList.size() + " Clusters has been initiated.");
	}

	// merge both clusters into one
	public static L_Cluster merge(L_Cluster c1, L_Cluster c2) {
		L_Cluster c = new L_Cluster();

		Iterator<Laptop> it1 = c1.getList().iterator();
		while (it1.hasNext()) {
			c.addItem(it1.next());
			c.updateIDValues();
		}
		Iterator<Laptop> it2 = c2.getList().iterator();
		while (it2.hasNext()) {
			c.addItem(it2.next());
			c.updateIDValues();
		}

		return c;
	}

	public static boolean hasBeenTested(L_Cluster c1, L_Cluster c2) {

		boolean found = false;

		// check if c1 and c2 has been checked yet
		if (clusterCheck.contains(c1.getID())) {
			found = true;
		} else if (clusterCheck.contains(c2.getID())) {
			found = true;
		} else {
			found = false;
		}
		return found;
	}

	public static boolean findOverlap(L_Cluster c1, L_Cluster c2) {

//		clusterCheck.add(c1.getID());
//		clusterCheck.add(c2.getID());

		boolean overlap = false;

		// get the corresponding idValues for both clusters
		HashSet<String> h1 = c1.getIDValues();
		HashSet<String> h2 = c2.getIDValues();

		for (String id : h1) {
			if ((id != "") && h2.contains(id)) {
				overlap = true;
//				System.out.println(id + " has been found.");
			}
		}
		return overlap;
	}

	// Compares clustersfrom clusterList, only if their are distinct and have not
	// been compared in the same round before
	public static void findClusters() {

		clusterCheck = new HashSet<String>();
		valuesToAdd = new HashSet<L_Cluster>();
		valuesToRemove = new HashSet<L_Cluster>();

		Iterator<L_Cluster> it1 = clusterList.iterator();
		int count = 0;

		while (it1.hasNext()) {
			L_Cluster c1 = it1.next();

			Iterator<L_Cluster> it2 = clusterList.iterator();

			count++;
//			System.out.println(count);

			if (!c1.getHasBeenMatched()) {
				while (it2.hasNext()) {
					L_Cluster c2 = it2.next();

					Iterator<String> iValue1 = c1.getIDValues().iterator();
					Iterator<String> iValue2 = c2.getIDValues().iterator();

					if ((iValue2.hasNext()) && (iValue1.hasNext())) {
						String s1 = iValue1.next();
						String s2 = iValue2.next();

						if ((s1.length() > 2) && (s2.length() > 2)) {
							char char_1_1 = s1.charAt(0);
							char char_2_1 = s2.charAt(0);
							char char_1_2 = s1.charAt(1);
							char char_2_2 = s2.charAt(1);

							// compares only if different clusters
							// and number starts with the same digit

							if ((char_1_1 == char_2_1) && (char_1_2 == char_2_2) && (c1.getID() != c2.getID())) {
//							
								if ((!c2.getHasBeenMatched())) {
									boolean test = findOverlap(c1, c2);

									if (test) {
										L_Cluster new_cluster = merge(c1, c2);
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
				}
			}
		}
		clusterList.removeAll(valuesToRemove);
		clusterList.addAll(valuesToAdd);
	}

	public static void clustering() {
		int size_before = clusterList.size();
		int size_after = 0;
//		int count = 12;
		int count = 0;

		while (size_before != size_after) {
//			 while (count > 0) {

			System.out.println("\n***** Find Clusters Round " + count);

			size_before = clusterList.size();

			findClusters();

			for (L_Cluster c : clusterList) {
				c.setHasBeenMatched(false);
			}

			size_after = clusterList.size();
			System.out.println("Clustersize now: " + size_before + " / " + size_after);
			count++;

		}
	}

	public static void printClusterFile(String filename) {

		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(filename));
			StringBuilder sb = new StringBuilder();
			sb.append("clusterID");
			sb.append(',');
			sb.append("Values");
			sb.append(',');
			sb.append("Size");
			sb.append(',');
			sb.append("IDs");
			sb.append('\n');
			for (L_Cluster c : clusterList) {
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

	public static void main(String[] args) {
		// resets all HashSets
		clearSets();

		// reads file into Dogfood HashSet
		System.out.println("\n***** New File Read *********\n");
		readDataLineByLine("data/input/laptops.csv");

		// turns dogfood items into single clusters
		System.out.println("\n***** Initiate Clusters *********\n");
		createCluster(itemList);

		System.out.println("\n***** Start Clustering *********\n");
		clustering();

		printClusterFile("data/output/clusters_laptops.csv");

	}	 
}
